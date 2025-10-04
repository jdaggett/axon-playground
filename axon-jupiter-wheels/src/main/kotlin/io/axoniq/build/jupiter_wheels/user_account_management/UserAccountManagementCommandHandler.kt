package io.axoniq.build.jupiter_wheels.user_account_management

import io.axoniq.build.jupiter_wheels.user_account_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

/**
 * Command handler for User Account Management component.
 * Handles user registration and email verification commands.
 */
class UserAccountManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserAccountManagementCommandHandler::class.java)
    }

    /**
     * Handles RegisterAccount command to create a new user account.
     * Given that the user does not exist, when the user registers account, then user account is created.
     */
    @CommandHandler
    fun handle(command: RegisterAccount, eventAppender: EventAppender): UserRegistrationResult {
        logger.info("Processing RegisterAccount command for email: {}", command.email)

        // Generate unique user ID
        val userId = UUID.randomUUID().toString()

        // Create user account created event
        val event = UserAccountCreated(
            userId = userId,
            email = command.email,
            phoneNumber = command.phoneNumber,
            name = command.name
        )

        eventAppender.append(event)
        logger.info("User account created with ID: {}", userId)
        
        return UserRegistrationResult(userId = userId)
    }

    /**
     * Handles VerifyEmail command to verify user's email address.
     * Given that user account is created, when the user verifies email, then email is verified.
     */
    @CommandHandler
    fun handle(
        command: VerifyEmail,
        @InjectEntity state: UserAccountManagementState,
        eventAppender: EventAppender
    ): EmailVerificationResult {
        logger.info("Processing VerifyEmail command for user: {}", command.userId)

        // Validate that user exists
        if (state.getUserId() == null) {
            logger.error("User not found: {}", command.userId)
            return EmailVerificationResult(verificationSuccessful = false)
        }

        // Check if email is already verified
        if (state.getEmailVerified()) {
            logger.info("Email already verified for user: {}", command.userId)
            return EmailVerificationResult(verificationSuccessful = true)
        }

        // Validate verification token
        if (state.getVerificationToken() != command.verificationToken) {
            logger.error("Invalid verification token for user: {}", command.userId)
            return EmailVerificationResult(verificationSuccessful = false)
        }

        // Create email verified event
        val event = EmailVerified(
            userId = command.userId,
            verificationDate = LocalDateTime.now()
        )

        eventAppender.append(event)
        logger.info("Email verified successfully for user: {}", command.userId)

        return EmailVerificationResult(verificationSuccessful = true)
    }
}

