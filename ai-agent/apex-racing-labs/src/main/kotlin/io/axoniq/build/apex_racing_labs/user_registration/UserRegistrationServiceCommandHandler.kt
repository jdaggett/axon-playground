package io.axoniq.build.apex_racing_labs.user_registration

import io.axoniq.build.apex_racing_labs.user_registration.api.*
import io.axoniq.build.apex_racing_labs.user_registration.exception.EmailAlreadyRegistered
import io.axoniq.build.apex_racing_labs.user_registration.exception.VerificationTokenExpired
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Command handler for the User Registration Service component.
 * Handles user account creation and email verification commands.
 */
class UserRegistrationServiceCommandHandler(
    private val commandGateway: CommandGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserRegistrationServiceCommandHandler::class.java)
        private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
        private const val EMAIL_VERIFICATION_TIMEOUT_HOURS = 24L
    }

    /**
     * Handles the CreateAccount command for the User Registration Service.
     * Creates a new user account with email verification requirement.
     * 
     * @param command The CreateAccount command containing user details
     * @param state The current state of the user registration entity
     * @param eventAppender Event appender to publish events
     * @param processingContext Processing context for command handling
     * @return AccountCreationResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: CreateAccount,
        @InjectEntity state: UserRegistrationState,
        eventAppender: EventAppender,
        processingContext: ProcessingContext
    ): AccountCreationResult {
        logger.info("Handling CreateAccount command for email: ${command.email}")

        // Check if email is already registered
        if (state.getEmail() != null) {
            logger.error("Account creation failed: email already registered - ${command.email}")
            throw EmailAlreadyRegistered("An account with this email address already exists")
        }

        // Generate verification token
        val verificationToken = UUID.randomUUID().toString()

        // Create and publish AccountCreated event
        val event = AccountCreated(
            email = command.email,
            verificationToken = verificationToken
        )
        eventAppender.append(event)

        // Schedule email verification deadline
        scheduleEmailVerificationDeadline(command.email, processingContext)

        logger.info("Account created successfully for email: ${command.email}")
        return AccountCreationResult(
            success = true,
            message = "Account created successfully. Please check your email for verification."
        )
    }

    /**
     * Handles the VerifyEmail command for the User Registration Service.
     * Verifies user email using the provided verification token.
     * 
     * @param command The VerifyEmail command containing verification token
     * @param state The current state of the user registration entity
     * @param eventAppender Event appender to publish events
     * @return EmailVerificationResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: VerifyEmail,
        @InjectEntity state: UserRegistrationState,
        eventAppender: EventAppender
    ): EmailVerificationResult {
        logger.info("Handling VerifyEmail command with token: ${command.verificationToken}")

        // Check if account exists and is not yet verified
        if (state.getEmail() == null) {
            logger.error("Email verification failed: no account found for token")
            throw VerificationTokenExpired("Invalid verification token")
        }

        if (state.getEmailVerified()) {
            logger.info("Email already verified for account: ${state.getEmail()}")
            return EmailVerificationResult(
                success = true,
                message = "Email is already verified"
            )
        }

        // Verify token matches
        if (state.getVerificationToken() != command.verificationToken) {
            logger.error("Email verification failed: invalid token")
            throw VerificationTokenExpired("Invalid or expired verification token")
        }

        // Check account status - if marked as unverified, token has expired
        if (state.getAccountStatus() == "UNVERIFIED") {
            logger.error("Email verification failed: account marked as unverified")
            throw VerificationTokenExpired("Verification token has expired")
        }

        // Create and publish EmailVerified event
        val event = EmailVerified(email = state.getEmail()!!)
        eventAppender.append(event)

        logger.info("Email verified successfully for: ${state.getEmail()}")
        return EmailVerificationResult(
            success = true,
            message = "Email verified successfully"
        )
    }

    /**
     * Handles the EmailVerificationDeadlineReached deadline for the User Registration Service.
     * Marks unverified accounts as unverified when verification deadline is reached.
     * 
     * @param deadline The EmailVerificationDeadlineReached deadline
     * @param state The current state of the user registration entity
     * @param eventAppender Event appender to publish events
     */
    @CommandHandler
    fun handle(
        deadline: EmailVerificationDeadlineReached,
        @InjectEntity state: UserRegistrationState,
        eventAppender: EventAppender
    ) {
        logger.info("Handling EmailVerificationDeadlineReached for account: ${state.getEmail()}")

        // Only mark as unverified if email is not yet verified
        if (!state.getEmailVerified() && state.getEmail() != null) {
            val event = AccountMarkedUnverified(email = state.getEmail()!!)
            eventAppender.append(event)
            logger.info("Account marked as unverified due to deadline: ${state.getEmail()}")
        }
    }

    /**
     * Schedules the email verification deadline using a virtual thread.
     * This is a temporary solution until native deadline support is available.
     * 
     * @param email The email address associated with the account
     * @param processingContext Processing context for command dispatching
     */
    private fun scheduleEmailVerificationDeadline(email: String, processingContext: ProcessingContext) {
        executor.submit {
            try {
                logger.debug("Scheduling email verification deadline for: $email")
                Thread.sleep(Duration.ofHours(EMAIL_VERIFICATION_TIMEOUT_HOURS).toMillis())

                val deadline = EmailVerificationDeadlineReached()
                commandGateway.send(deadline, processingContext)
                logger.info("Email verification deadline triggered for: $email")
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.warn("Email verification deadline scheduling interrupted for: $email")
            }
        }
    }
}

