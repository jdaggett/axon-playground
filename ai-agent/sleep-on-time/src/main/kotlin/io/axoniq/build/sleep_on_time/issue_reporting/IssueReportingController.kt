package io.axoniq.build.sleep_on_time.issue_reporting

import io.axoniq.build.sleep_on_time.issue_reporting.api.ReportContainerIssue
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Issue Reporting Service component.
 * Provides HTTP endpoints for reporting container issues.
 */
@RestController
@RequestMapping("/api/issue-reporting")
class IssueReportingController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(IssueReportingController::class.java)
    }

    /**
     * Endpoint for reporting container issues.
     * Accepts issue report data and dispatches ReportContainerIssue command.
     */
    @PostMapping("/report")
    fun reportContainerIssue(@RequestBody request: ReportContainerIssueRequest): ResponseEntity<String> {
        val command = ReportContainerIssue(
            bookingId = request.bookingId,
            issueType = request.issueType,
            guestId = request.guestId,
            description = request.description,
            severity = request.severity,
            containerId = request.containerId
        )

        logger.info("Dispatching ReportContainerIssue command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Issue report accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ReportContainerIssue command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report container issue")
        }
    }
}

/**
 * Request data class for the report container issue endpoint.
 */
data class ReportContainerIssueRequest(
    val bookingId: String,
    val issueType: String,
    val guestId: String,
    val description: String,
    val severity: String,
    val containerId: String
)

