package io.axoniq.build.jupiter_wheels.email_verification_system

import io.axoniq.build.jupiter_wheels.email_verification_system.api.UserAccountCreated
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Email Verification System Integration - External System Component
 * 
 * This component handles integration with the external email verification system.
 * It processes UserAccountCreated events and triggers verification email sending.
 * This is a stub implementation that logs the intended action.
 */
@Service
class EmailVerificationSystemIntegration(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(EmailVerificationSystemIntegration::class.java)

    /**
     * Send Verification - External System Action
     * 
     * Handles UserAccountCreated events to trigger verification email sending through
     * the external email verification system. Currently logs the action as a stub implementation.
     *
     * @param event The UserAccountCreated event containing user details
     * @param processingContext The processing context for command gateway operations
     */
    @EventHandler
    fun sendVerification(event: UserAccountCreated, processingContext: ProcessingContext) {
        logger.info("Handling external system action: Send verification email to user {} at {}",    event.userId, event.email)

        logger.info("Would send verification email to: {} ({})", event.name, event.email)
        logger.info("User ID: {}, Phone: {}", event.userId, event.phoneNumber)

        // TODO: Integrate with actual email verification service
        // This is a stub implementation that will be replaced with actual external system integration
    }
}