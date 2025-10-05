package io.axoniq.build.apex_racing_labs.user_setup

import io.axoniq.build.apex_racing_labs.user_setup.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for User Setup Service component.
 * Provides endpoints for completing user initial setup with preferences.
 */
@RestController
@RequestMapping("/api/user-setup")
class UserSetupServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserSetupServiceController::class.java)
    }

    /**
     * Completes the initial setup for a user with their preferences.
     * Allows users to specify their favorite team and driver during initial setup.
     *
     * @param request The CompleteInitialSetup request containing user preferences
     * @return ResponseEntity indicating success or failure of the setup completion
     */
    @PostMapping("/complete")
    fun completeInitialSetup(@RequestBody request: CompleteInitialSetup): ResponseEntity<String> {
        logger.info("Dispatching CompleteInitialSetup command for user: ${request.userId}")
        return try {
            commandGateway.sendAndWait(request)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("User setup completion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CompleteInitialSetup command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to complete user setup")
        }
    }
}