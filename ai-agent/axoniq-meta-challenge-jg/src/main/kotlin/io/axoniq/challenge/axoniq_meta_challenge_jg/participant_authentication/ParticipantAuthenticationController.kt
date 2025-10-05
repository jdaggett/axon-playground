package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication

import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Participant Authentication component.
 * Exposes HTTP endpoints for participant authentication operations.
 */
@RestController
@RequestMapping("/api/participant-authentication")
class ParticipantAuthenticationController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ParticipantAuthenticationController::class.java)
    }

    /**
     * Endpoint for requesting password reset.
     */
    @PostMapping("/password-reset")
    fun requestPasswordReset(@RequestBody request: RequestPasswordResetRequest): ResponseEntity<String> {
        val command = RequestPasswordReset(
            email = request.email
        )
        logger.info("Dispatching RequestPasswordReset command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Password reset request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RequestPasswordReset command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process password reset request")
        }
    }

    /**
     * Endpoint for creating new participant accounts.
     */
    @PostMapping("/create-account")
    fun createAccount(@RequestBody request: CreateAccountRequest): ResponseEntity<String> {
        val command = CreateAccount(
            password = request.password,
            firstName = request.firstName,
            email = request.email,
            lastName = request.lastName
        )
        logger.info("Dispatching CreateAccount command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Account creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateAccount command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create account")
        }
    }

    /**
     * Endpoint for GitHub authentication.
     */
    @PostMapping("/login/github")
    fun loginWithGitHub(@RequestBody request: LoginWithGitHubRequest): ResponseEntity<String> {
        val command = LoginWithGitHub(
            githubToken = request.githubToken
        )
        logger.info("Dispatching LoginWithGitHub command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("GitHub login accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch LoginWithGitHub command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to login with GitHub")
        }
    }

    /**
     * Endpoint for credential-based authentication.
     */
    @PostMapping("/login/credentials")
    fun loginWithCredentials(@RequestBody request: LoginWithCredentialsRequest): ResponseEntity<String> {
        val command = LoginWithCredentials(
            password = request.password,
            email = request.email
        )
        logger.info("Dispatching LoginWithCredentials command: {}", command.email)
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Credential login accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch LoginWithCredentials command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to login with credentials")
        }
    }
}

/**
 * Request DTOs for REST endpoints
 */
data class RequestPasswordResetRequest(
    val email: String
)

data class CreateAccountRequest(
    val password: String,
    val firstName: String,
    val email: String,
    val lastName: String
)

data class LoginWithGitHubRequest(
    val githubToken: String
)

data class LoginWithCredentialsRequest(
    val password: String,
    val email: String
)

