package io.axoniq.build.jupiter_wheels.user_account_management

import io.axoniq.build.jupiter_wheels.user_account_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for User Account Management component.
 * Provides endpoints for user registration and email verification.
 */
@RestController
@RequestMapping("/api/user-account")
class UserAccountManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserAccountManagementController::class.java)
    }

    /**
     * Endpoint to register a new user account.
     * Accepts user registration data and dispatches RegisterAccount command.
     */
    @PostMapping("/register")
    fun registerAccount(@RequestBody request: RegisterAccountRequest): ResponseEntity<String> {
        val command = RegisterAccount(
            email = request.email,
            phoneNumber = request.phoneNumber,
            name = request.name
        )

        logger.info("Dispatching RegisterAccount command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Account registration accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RegisterAccount command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register account")
        }
    }

    /**
     * Endpoint to verify user's email address.
     * Accepts verification data and dispatches VerifyEmail command.
     */
    @PostMapping("/verify-email")
    fun verifyEmail(@RequestBody request: VerifyEmailRequest): ResponseEntity<String> {
        val command = VerifyEmail(
            userId = request.userId,
            verificationToken = request.verificationToken
        )

        logger.info("Dispatching VerifyEmail command: $command")
        
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Email verification accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch VerifyEmail command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to verify email")
        }
    }

    /**
     * Request data class for account registration endpoint.
     */
    data class RegisterAccountRequest(
        val email: String,
        val phoneNumber: String,
        val name: String
    )

    /**
     * Request data class for email verification endpoint.
     */
    data class VerifyEmailRequest(
        val userId: String,
        val verificationToken: String
    )
}

