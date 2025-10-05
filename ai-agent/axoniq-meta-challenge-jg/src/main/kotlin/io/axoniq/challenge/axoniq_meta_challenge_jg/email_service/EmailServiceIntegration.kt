package io.axoniq.challenge.axoniq_meta_challenge_jg.email_service

import io.axoniq.challenge.axoniq_meta_challenge_jg.email_service.api.PasswordResetEmailSent
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Email Service Integration - External System Component
 * 
 * This component handles the integration with external email services to send
 * password reset emails to participants. It responds to PasswordResetEmailSent
 * events by triggering the appropriate email sending action.
 * 
 * Component: Email Service Integration (email-service)
 * Type: EXTERNAL_SYSTEM
 * Purpose: Handles sending password reset emails to participants
 */
@Service
class EmailServiceIntegration {

    private val logger: Logger = LoggerFactory.getLogger(EmailServiceIntegration::class.java)
    
    /**
     * Handles the PasswordResetEmailSent event by triggering the external email service
     * to send a password reset email to the specified participant.
     * 
     * This is a stub implementation that logs the action that would be performed
     * by the external email service integration.
     * 
     * @param event The PasswordResetEmailSent event containing participant ID and email
     * @param processingContext The processing context for the event
     */
    @EventHandler
    fun handle(event: PasswordResetEmailSent, processingContext: ProcessingContext) {
        logger.info("Handling password reset email send request for participant: ${event.participantId}")
        logger.info("Sending password reset email to: ${event.email}")

        // In a real implementation, this would:
        // 1. Connect to external email service (e.g., SendGrid, AWS SES, SMTP)
        // 2. Generate password reset email template with participant details
        // 3. Send the email to the specified address
        // 4. Handle any potential failures or confirmations

        logger.debug("Password reset email would be sent to ${event.email} for participant ${event.participantId}")
    }
}