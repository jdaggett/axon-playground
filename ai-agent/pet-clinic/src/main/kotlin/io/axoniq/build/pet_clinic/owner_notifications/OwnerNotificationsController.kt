package io.axoniq.build.pet_clinic.owner_notifications

import io.axoniq.build.pet_clinic.owner_notifications.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Owner Notifications - REST controller for owner notification operations
 * Exposes HTTP endpoints for managing owner notifications
 */
@RestController
@RequestMapping("/api/owner-notifications")
class OwnerNotificationsController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OwnerNotificationsController::class.java)
    }

    /**
     * Owner Notifications - Endpoint to notify an owner
     * Accepts a NotifyOwner command and dispatches it for processing
     */
    @PostMapping("/notify")
    fun notifyOwner(@RequestBody request: NotifyOwnerRequest): ResponseEntity<String> {
        val command = NotifyOwner(
            ownerEmail = request.ownerEmail,
            petId = request.petId
        )

        logger.info("Dispatching NotifyOwner command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Owner notification request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch NotifyOwner command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process owner notification")
        }
    }
}

/**
 * Owner Notifications - Request model for notifying owner endpoint
 */
data class NotifyOwnerRequest(
    val ownerEmail: String,
    val petId: String
)