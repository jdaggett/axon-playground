package io.axoniq.build.sleep_on_time.notification_service

import io.axoniq.build.sleep_on_time.notification_service.api.ContainerIssueReported
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Notification Service - External System Component
 * 
 * This service handles notifications to property owners when container issues are reported.
 * It listens for ContainerIssueReported events and performs external system actions
 * to alert the appropriate property owners about the issues.
 */
@Service
class NotificationService {
    
    private val logger: Logger = LoggerFactory.getLogger(NotificationService::class.java)

    /**
     * Handles ContainerIssueReported events to alert property owners
     * 
     * This method processes container issue reports and performs the external system action
     * "NotificationSystemAlertOwner" to notify property owners about issues with their containers.
     * 
     * @param event The ContainerIssueReported event containing issue details
     * @param processingContext The processing context for the event
     */
    @EventHandler
    fun handle(event: ContainerIssueReported, processingContext: ProcessingContext) {
        logger.info("Handling container issue notification for issue ID: ${event.issueId}")
        logger.info("Container issue details - Type: ${event.issueType}, Severity: ${event.severity}, Container: ${event.containerId}")
        logger.info("Booking ID: ${event.bookingId}, Guest ID: ${event.guestId}")
        logger.info("Issue reported at: ${event.reportedAt}")
        logger.info("Description: ${event.description}")
        
        // Log the external system action that would be performed
        logger.info("Performing external system action: NotificationSystemAlertOwner")
        logger.info("This would notify the property owner about the container issue with severity: ${event.severity}")
        
        // In a real implementation, this would:
        // 1. Look up property owner contact information based on the container/booking
        // 2. Format notification message with issue details
        // 3. Send notification via email, SMS, or push notification
        // 4. Track notification delivery status
    }
}