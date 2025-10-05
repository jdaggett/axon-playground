package io.axoniq.build.dance_test.calendly_integration

import io.axoniq.build.dance_test.calendly_integration.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Calendly Webhook Integration - External System Component
 * 
 * This component receives webhook notifications from the Calendly booking system
 * and processes them to create corresponding actions in the dance lesson booking system.
 * It handles session scheduling, modifications, and cancellations from Calendly.
 */
@Service
class CalendlyWebhookIntegration(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(CalendlyWebhookIntegration::class.java)

    /**
     * Handles SessionScheduled events from Calendly to process new bookings.
     * 
     * When a session is scheduled in Calendly, this handler processes the booking
     * and creates a session booking in our system.
     * 
     * @param event The SessionScheduled event containing session details
     * @param processingContext The processing context for command routing
     */
    @EventHandler
    fun handleCalendlyProcessBooking(event: SessionScheduled, processingContext: ProcessingContext) {
        logger.info("Processing Calendly booking for session ${event.sessionId} with instructor ${event.instructorId} and student ${event.studentId}")

        // Create session booking command based on the scheduled session
        val createBookingCommand = CreateSessionBooking(
            instructorId = event.instructorId,
            duration = event.duration,
            sessionDate = event.sessionDate,
            studentId = event.studentId,
            sessionId = event.sessionId
        )

        logger.debug("Sending CreateSessionBooking command for session ${event.sessionId}")
        commandGateway.send(createBookingCommand, processingContext)
    }

    /**
     * Handles SessionDetailsModified events from Calendly webhook notifications.
     * 
     * When session details are modified in Calendly, this handler processes the webhook
     * notification and logs the modification details.
     * 
     * @param event The SessionDetailsModified event containing modification details
     * @param processingContext The processing context for command routing
     */
    @EventHandler
    fun handleCalendlyWebhookReceived(event: SessionDetailsModified, processingContext: ProcessingContext) {
        logger.info("Received Calendly webhook notification for session ${event.sessionId} modification")

        if (event.newDuration != null) {
            logger.info("Session ${event.sessionId} duration modified to ${event.newDuration} minutes")
        }

        if (event.newSessionDate != null) {
            logger.info("Session ${event.sessionId} date modified to ${event.newSessionDate}")
        }

        logger.debug("Calendly webhook processing completed for session ${event.sessionId}")
    }

    /**
     * Handles SessionCancelled events from Calendly to process cancellations.
     * 
     * When a session is cancelled in Calendly, this handler processes the cancellation
     * and creates a cancellation command in our system.
     * 
     * @param event The SessionCancelled event containing cancellation details
     * @param processingContext The processing context for command routing
     */
    @EventHandler
    fun handleCalendlyProcessCancellation(event: SessionCancelled, processingContext: ProcessingContext) {
        logger.info("Processing Calendly cancellation for session ${event.sessionId} at ${event.cancellationTime}")

        // Create session cancellation command based on the cancelled session
        val cancelBookingCommand = CancelSessionBooking(
            cancellationTime = event.cancellationTime,
            sessionId = event.sessionId
        )

        logger.debug("Sending CancelSessionBooking command for session ${event.sessionId}")
        commandGateway.send(cancelBookingCommand, processingContext)
    }
}