package io.axoniq.build.sleep_on_time.issue_tracking_service

import io.axoniq.build.sleep_on_time.issue_tracking_service.api.ContainerIssueReported
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Issue Tracking Service - External System Component
 * 
 * This component handles external system integration for creating issue entries
 * in an external tracking system when container issues are reported.
 * 
 * Component: Issue Tracking Service (EXTERNAL_SYSTEM)
 * External Action: IssueTrackingCreateEntry
 */
@Service
class IssueTrackingService {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(IssueTrackingService::class.java)
    }

    /**
     * Handles ContainerIssueReported events by creating entries in the external issue tracking system.
     * 
     * This method processes reported container issues and creates corresponding entries
     * in an external tracking system for follow-up and resolution tracking.
     * 
     * @param event The ContainerIssueReported event containing issue details
     * @param processingContext The processing context for the current unit of work
     */
    @EventHandler
    fun handle(event: ContainerIssueReported, processingContext: ProcessingContext) {
        logger.info("Processing container issue report for external tracking system")
        logger.info("Issue ID: ${event.issueId}, Container ID: ${event.containerId}, Booking ID: ${event.bookingId}")
        logger.info("Issue Type: ${event.issueType}, Severity: ${event.severity}")
        logger.info("Guest ID: ${event.guestId}, Description: ${event.description}")
        logger.info("Reported At: ${event.reportedAt}")
        
        // Log the external system action to be performed
        logger.info("Creating issue entry in external tracking system for issue: ${event.issueId}")
        logger.info("External action: IssueTrackingCreateEntry")

        // TODO: Implement actual external system integration
        // This would typically involve:
        // 1. Formatting the issue data for the external system
        // 2. Making an API call to create the issue entry
        // 3. Handling the response and potential error conditions
        // 4. Optionally sending a command back to the system with the external system's response

        logger.debug("Issue tracking entry creation completed for issue: ${event.issueId}")
    }
}