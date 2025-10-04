package io.axoniq.build.jupiter_wheels.bike_replacement_management

import io.axoniq.build.jupiter_wheels.bike_replacement_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Bike Replacement Management component.
 * Exposes endpoints for bike replacement operations.
 */
@RestController
@RequestMapping("/api/bike-replacements")
class BikeReplacementManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeReplacementManagementController::class.java)
    }

    /**
     * Endpoint to request a bike replacement.
     * Accepts bike replacement request details.
     */
    @PostMapping("/request")
    fun requestBikeReplacement(@RequestBody request: RequestBikeReplacementRequest): ResponseEntity<String> {
        val command = RequestBikeReplacement(
            originalBikeId = request.originalBikeId,
            rentalId = request.rentalId,
            issueDescription = request.issueDescription
        )
        logger.info("Dispatching RequestBikeReplacement command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Bike replacement request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RequestBikeReplacement command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to request bike replacement")
        }
    }
    
    /**
     * Endpoint to assign a replacement bike.
     * Accepts replacement bike assignment details.
     */
    @PostMapping("/assign")
    fun assignReplacementBike(@RequestBody request: AssignReplacementBikeRequest): ResponseEntity<String> {
        val command = AssignReplacementBike(
            replacementBikeId = request.replacementBikeId,
            rentalId = request.rentalId
        )
        logger.info("Dispatching AssignReplacementBike command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Replacement bike assignment accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AssignReplacementBike command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to assign replacement bike")
        }
    }

    /**
     * Request DTO for bike replacement request.
     */
    data class RequestBikeReplacementRequest(
        val originalBikeId: String,
        val rentalId: String,
        val issueDescription: String
    )

    /**
     * Request DTO for replacement bike assignment.
     */
    data class AssignReplacementBikeRequest(
        val replacementBikeId: String,
        val rentalId: String
    )
}

