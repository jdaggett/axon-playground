package io.axoniq.build.sleep_on_time.issue_reporting

import io.axoniq.build.sleep_on_time.issue_reporting.api.ReportContainerIssue
import io.axoniq.build.sleep_on_time.issue_reporting.api.IssueReportResult
import io.axoniq.build.sleep_on_time.issue_reporting.api.ContainerIssueReported
import io.axoniq.build.sleep_on_time.issue_reporting.exception.InvalidIssueData
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

/**
 * Command handler for the Issue Reporting Service component.
 * Handles container issue reporting commands from guests and validates
 * the provided issue data before publishing events.
 */
class IssueReportingCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(IssueReportingCommandHandler::class.java)
        private val VALID_SEVERITIES = setOf("LOW", "MEDIUM", "HIGH", "CRITICAL")
        private val VALID_ISSUE_TYPES = setOf("MAINTENANCE", "CLEANLINESS", "SAFETY", "AMENITIES", "OTHER")
    }

    /**
     * Handles the ReportContainerIssue command.
     * Validates issue data and publishes ContainerIssueReported event if valid.
     * Returns IssueReportResult indicating success or failure.
     */
    @CommandHandler
    fun handle(
        command: ReportContainerIssue,
        @InjectEntity state: IssueReportingState,
        eventAppender: EventAppender
    ): IssueReportResult {
        logger.info("Processing ReportContainerIssue command for guest ${command.guestId} and container ${command.containerId}")

        // Validate that issue hasn't already been reported for this booking/guest/container combination
        if (state.getIssueReported()) {
            logger.warn("Issue already reported for booking ${command.bookingId}")
            throw InvalidIssueData("Issue has already been reported for this booking")
        }

        // Validate required fields
        if (command.description.isBlank()) {
            logger.warn("Invalid issue data: description cannot be empty")
            throw InvalidIssueData("Issue description cannot be empty")
        }

        if (command.severity !in VALID_SEVERITIES) {
            logger.warn("Invalid issue data: invalid severity ${command.severity}")
            throw InvalidIssueData("Invalid severity level. Must be one of: ${VALID_SEVERITIES.joinToString()}")
        }

        if (command.issueType !in VALID_ISSUE_TYPES) {
            logger.warn("Invalid issue data: invalid issue type ${command.issueType}")
            throw InvalidIssueData("Invalid issue type. Must be one of: ${VALID_ISSUE_TYPES.joinToString()}")
        }

        // Generate unique issue ID
        val issueId = UUID.randomUUID().toString()

        // Create and append the issue reported event
        val event = ContainerIssueReported(
            issueId = issueId,
            bookingId = command.bookingId,
            issueType = command.issueType,
            guestId = command.guestId,
            description = command.description,
            reportedAt = LocalDateTime.now(),
            severity = command.severity,
            containerId = command.containerId
        )

        logger.info("Publishing ContainerIssueReported event for issue $issueId")
        eventAppender.append(event)

        return IssueReportResult(
            success = true,
            issueId = issueId
        )
    }
}

