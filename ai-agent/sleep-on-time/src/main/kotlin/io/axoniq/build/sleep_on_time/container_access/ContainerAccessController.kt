package io.axoniq.build.sleep_on_time.container_access

import io.axoniq.build.sleep_on_time.container_access.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

/**
 * Container Access Controller - REST API for Container Access Management component
 * Exposes endpoints for container access operations
 */
@RestController
@RequestMapping("/api/container-access")
class ContainerAccessController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ContainerAccessController::class.java)
    }

    /**
     * Opens container door for guest access
     */
    @PostMapping("/open-door")
    fun openContainerDoor(@RequestBody request: OpenContainerDoorRequest): ResponseEntity<String> {
        val command = OpenContainerDoor(
            bookingId = request.bookingId,
            guestId = request.guestId,
            data = request.data,
            containerId = request.containerId
        )
        logger.info("Dispatching OpenContainerDoor command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Door opening request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch OpenContainerDoor command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to open container door")
        }
    }
    
    /**
     * Confirms that container door has been unlocked
     */
    @PostMapping("/confirm-unlocked")
    fun confirmDoorUnlocked(@RequestBody request: ConfirmDoorUnlockedRequest): ResponseEntity<String> {
        val command = ConfirmDoorUnlocked(
            bookingId = request.bookingId,
            guestId = request.guestId,
            unlockTimestamp = request.unlockTimestamp,
            containerId = request.containerId
        )
        logger.info("Dispatching ConfirmDoorUnlocked command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Door unlock confirmation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ConfirmDoorUnlocked command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm door unlock")
        }
    }

    /**
     * Obtains/reserves a container for guest
     */
    @PostMapping("/obtain")
    fun obtainContainer(@RequestBody request: ObtainContainerRequest): ResponseEntity<String> {
        val command = ObtainContainer(
            bookingId = request.bookingId,
            guestId = request.guestId,
            containerId = request.containerId
        )
        logger.info("Dispatching ObtainContainer command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Container obtain request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ObtainContainer command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to obtain container")
        }
    }

    // Request DTOs
    data class OpenContainerDoorRequest(
        val bookingId: String,
        val guestId: String,
        val data: Int,
        val containerId: String
    )

    data class ConfirmDoorUnlockedRequest(
        val bookingId: String,
        val guestId: String,
        val unlockTimestamp: LocalDateTime,
        val containerId: String
    )

    data class ObtainContainerRequest(
        val bookingId: String,
        val guestId: String,
        val containerId: String
    )
}

