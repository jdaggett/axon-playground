package io.axoniq.build.jupiter_wheels.rental_extension_management

import io.axoniq.build.jupiter_wheels.rental_extension_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Rental Extension Management component.
 * Exposes endpoints for rental extension request and approval operations.
 */
@RestController
@RequestMapping("/api/rental-extensions")
class RentalExtensionManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RentalExtensionManagementController::class.java)
    }

    /**
     * Request rental extension endpoint
     */
    @PostMapping("/request")
    fun requestRentalExtension(@RequestBody request: RequestRentalExtensionRequest): ResponseEntity<String> {
        val command = RequestRentalExtension(
            additionalTime = request.additionalTime,
            rentalId = request.rentalId
        )
        logger.info("Dispatching RequestRentalExtension command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Rental extension request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RequestRentalExtension command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to request rental extension")
        }
    }
    
    /**
     * Approve rental extension endpoint
     */
    @PostMapping("/approve")
    fun approveRentalExtension(@RequestBody request: ApproveRentalExtensionRequest): ResponseEntity<String> {
        val command = ApproveRentalExtension(
            approvedTime = request.approvedTime,
            rentalId = request.rentalId
        )
        logger.info("Dispatching ApproveRentalExtension command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Rental extension approval accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ApproveRentalExtension command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to approve rental extension")
        }
    }
}

/**
 * Request DTOs for REST endpoints
 */
data class RequestRentalExtensionRequest(
    val additionalTime: Int,
    val rentalId: String
)

data class ApproveRentalExtensionRequest(
    val approvedTime: Int,
    val rentalId: String
)

