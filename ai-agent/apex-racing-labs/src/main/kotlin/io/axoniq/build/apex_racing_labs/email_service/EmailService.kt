package io.axoniq.build.apex_racing_labs.email_service

import io.axoniq.build.apex_racing_labs.email_service.api.AccountCreated
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Email Service - External System Component
 * 
 * This external system component handles email notifications by listening to events
 * and triggering appropriate email actions. Currently configured to send verification
 * emails when accounts are created.
 * 
 * Component: Email Service (EXTERNAL_SYSTEM)
 * Purpose: Handles external email notifications
 */
@Service
class EmailService(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(EmailService::class.java)

    /**
     * Handles AccountCreated events to send verification emails
     * 
     * This method processes AccountCreated events and initiates the process of sending
     * a verification email to the newly created account. The email will contain the
     * verification token required for account activation.
     * 
     * External Action: Send Verification Email
     * Triggered by: AccountCreated event
     *
     * @param event The AccountCreated event containing user email and verification token
     * @param processingContext The processing context for command gateway operations
     */
    @EventHandler
    fun sendVerificationEmail(event: AccountCreated, processingContext: ProcessingContext) {
        logger.info("Processing AccountCreated event for email: ${event.email}")
        logger.info("Initiating verification email send process with token: ${event.verificationToken}")

        // Log the external system action that would be performed
        logger.info("External System Action: Sending verification email to ${event.email} with verification token ${event.verificationToken}")
        
        // In a real implementation, this would:
        // 1. Format the verification email template
        // 2. Send the email via email service provider (SMTP, SendGrid, etc.)
        // 3. Handle any email sending responses or failures
        // 4. Potentially send commands back to the system based on email delivery status

        logger.debug("Verification email process completed for account: ${event.email}")
    }
}