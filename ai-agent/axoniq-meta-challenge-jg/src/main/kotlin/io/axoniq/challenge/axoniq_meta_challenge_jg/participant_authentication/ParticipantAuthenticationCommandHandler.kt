package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication

import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.exception.InvalidCredentials
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.exception.SessionExpired
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for Participant Authentication component.
 * Handles participant login, account creation, and password reset functionality.
 */
class ParticipantAuthenticationCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ParticipantAuthenticationCommandHandler::class.java)
    }

    /**
     * Command handler for RequestPasswordReset.
     * Processes password reset requests and sends notification emails.
     */
    @CommandHandler
    fun handle(
        command: RequestPasswordReset,
        @InjectEntity state: ParticipantAuthenticationState,
        eventAppender: EventAppender
    ): PasswordResetResult {
        logger.info("Processing password reset request for email: {}", command.email)

        // Generate participant ID if not exists
        val participantId = state.getParticipantId() ?: UUID.randomUUID().toString()

        val event = PasswordResetEmailSent(
            participantId = participantId,
            email = command.email
        )
        eventAppender.append(event)

        logger.info("Password reset email sent for participant: {}", participantId)
        return PasswordResetResult(isSuccessful = true)
    }

    /**
     * Command handler for CreateAccount.
     * Creates new participant accounts and authenticates them.
     */
    @CommandHandler
    fun handle(
        command: CreateAccount,
        @InjectEntity state: ParticipantAuthenticationState,
        eventAppender: EventAppender
    ): AccountCreationResult {
        logger.info("Creating account for email: {}", command.email)

        // Check if participant is already authenticated
        if (state.getIsAuthenticated()) {
            logger.warn("Session expired during account creation for: {}", command.email)
            throw SessionExpired("Current session has expired")
        }

        val participantId = UUID.randomUUID().toString()

        val event = ParticipantAuthenticated(
            participantId = participantId,
            email = command.email,
            authenticationMethod = "credentials"
        )
        eventAppender.append(event)

        logger.info("Account created successfully for participant: {}", participantId)
        return AccountCreationResult(
            participantId = participantId,
            isSuccessful = true
        )
    }

    /**
     * Command handler for LoginWithGitHub.
     * Authenticates participants using GitHub OAuth tokens.
     */
    @CommandHandler
    fun handle(
        command: LoginWithGitHub,
        @InjectEntity state: ParticipantAuthenticationState,
        eventAppender: EventAppender
    ): ParticipantLoginResult {
        logger.info("Processing GitHub login with token")

        // Validate session state
        if (state.getIsAuthenticated() && state.getAuthenticationMethod() != "github") {
            logger.warn("Session expired during GitHub login")
            throw SessionExpired("Current session has expired")
        }

        // Generate participant ID based on GitHub token
        val participantId = "github_" + command.githubToken.hashCode().toString()

        val event = ParticipantAuthenticated(
            participantId = participantId,
            email = "$participantId@github.local", // Mock email for GitHub users
            authenticationMethod = "github"
        )
        eventAppender.append(event)

        logger.info("GitHub login successful for participant: {}", participantId)
        return ParticipantLoginResult(
            participantId = participantId,
            isSuccessful = true
        )
    }

    /**
     * Command handler for LoginWithCredentials.
     * Authenticates participants using email and password credentials.
     */
    @CommandHandler
    fun handle(
        command: LoginWithCredentials,
        @InjectEntity state: ParticipantAuthenticationState,
        eventAppender: EventAppender
    ): ParticipantLoginResult {
        logger.info("Processing credential login for email: {}", command.email)

        // Validate session state
        if (state.getIsAuthenticated() && state.getAuthenticationMethod() != "credentials") {
            logger.warn("Session expired during credential login for: {}", command.email)
            throw SessionExpired("Current session has expired")
        }

        // Mock credential validation - in real system would validate against stored credentials
        if (command.password.isBlank()) {
            logger.warn("Invalid credentials provided for email: {}", command.email)
            throw InvalidCredentials("Invalid email or password")
        }

        val participantId = state.getParticipantId() ?: UUID.randomUUID().toString()

        val event = ParticipantAuthenticated(
            participantId = participantId,
            email = command.email,
            authenticationMethod = "credentials"
        )
        eventAppender.append(event)

        logger.info("Credential login successful for participant: {}", participantId)
        return ParticipantLoginResult(
            participantId = participantId,
            isSuccessful = true
        )
    }
}

