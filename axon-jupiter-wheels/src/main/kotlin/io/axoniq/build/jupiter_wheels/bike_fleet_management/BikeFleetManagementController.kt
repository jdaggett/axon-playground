package io.axoniq.build.jupiter_wheels.bike_fleet_management

import io.axoniq.build.jupiter_wheels.bike_fleet_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for Bike Fleet Management component.
 * Exposes endpoints for bike creation and removal operations.
 */
@RestController
@RequestMapping("/api/bike-fleet")
class BikeFleetManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeFleetManagementController::class.java)
    }

    /**
     * Endpoint to create a new bike in the fleet
     */
    @PostMapping("/bikes")
    fun createNewBike(@RequestBody request: CreateNewBikeRequest): ResponseEntity<String> {
        val command = CreateNewBike(
            location = request.location,
            bikeType = request.bikeType,
            condition = request.condition
        )
        logger.info("Dispatching CreateNewBike command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Bike creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateNewBike command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create new bike")
        }
    }

    /**
     * Endpoint to remove a bike from the fleet
     */
    @DeleteMapping("/bikes/{bikeId}")
    fun removeBikeFromFleet(
        @PathVariable bikeId: String,
        @RequestBody request: RemoveBikeFromFleetRequest
    ): ResponseEntity<String> {
        val command = RemoveBikeFromFleet(
            bikeId = bikeId,
            removalReason = request.removalReason
        )
        logger.info("Dispatching RemoveBikeFromFleet command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Bike removal accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RemoveBikeFromFleet command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove bike from fleet")
        }
    }

    /**
     * Request DTO for creating a new bike
     */
    data class CreateNewBikeRequest(
        val location: String,
        val bikeType: String,
        val condition: String
    )

    /**
     * Request DTO for removing a bike from fleet
     */
    data class RemoveBikeFromFleetRequest(
        val removalReason: String
    )
}

