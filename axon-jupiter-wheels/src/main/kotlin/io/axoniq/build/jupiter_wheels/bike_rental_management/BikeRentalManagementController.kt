package io.axoniq.build.jupiter_wheels.bike_rental_management

import io.axoniq.build.jupiter_wheels.bike_rental_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Bike Rental Management component
 * Exposes endpoints for bike rental operations
 */
@RestController
@RequestMapping("/api/bike-rental")
class BikeRentalManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeRentalManagementController::class.java)
    }

    /**
     * Request data class for bike rental endpoint
     */
    data class RequestBikeRentalRequest(
        val userId: String,
        val bikeId: String
    )

    /**
     * Endpoint to request a bike rental
     */
    @PostMapping("/request")
    fun requestBikeRental(@RequestBody request: RequestBikeRentalRequest): ResponseEntity<String> {
        val command = RequestBikeRental(
            userId = request.userId,
            bikeId = request.bikeId
        )
        
        logger.info("Dispatching RequestBikeRental command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Bike rental request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RequestBikeRental command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to request bike rental: ${ex.message}")
        }
    }
}

