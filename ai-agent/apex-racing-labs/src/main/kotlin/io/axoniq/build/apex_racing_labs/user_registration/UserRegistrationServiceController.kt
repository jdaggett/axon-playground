package io.axoniq.build.apex_racing_labs.user_registration

import io.axoniq.build.apex_racing_labs.user_registration.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the User Registration Service component.
 * Provides HTTP endpoints for user account creation and email verification.
 */
@RestController
@RequestMapping("/api/user-registration")
class UserRegistrationServiceController(
    private val commandGateway: CommandGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserRegistrationServiceController::class.java)
    }

    /**
     * HTTP endpoint for creating a new user account.
     * Accepts CreateAccount command and dispatches it for processing.
     * 
     * @param request The CreateAccount command containing user details
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/create-account")
    fun createAccount(@RequestBody request: CreateAccount): ResponseEntity<String> {
        logger.info("Received create account request for email: ${request.email}")

        return try {
            commandGateway.sendAndWait(request)
            logger.info("Account creation accepted for email: ${request.email}")
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Account creation request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to process account creation request", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create account: ${ex.message}")
        }
    }

    /**
     * HTTP endpoint for verifying user email.
     * Accepts VerifyEmail command and dispatches it for processing.
     * 
     * @param request The VerifyEmail command containing verification token
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/verify-email")
    fun verifyEmail(@RequestBody request: VerifyEmail): ResponseEntity<String> {
        logger.info("Received email verification request with token: ${request.verificationToken}")

        return try {
            commandGateway.sendAndWait(request)
            logger.info("Email verification accepted for token: ${request.verificationToken}")
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Email verification request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to process email verification request", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify email: ${ex.message}")
        }
    }
}

