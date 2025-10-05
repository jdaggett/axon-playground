package io.axoniq.build.jupiter_wheels.bike_usage_management

import io.axoniq.build.jupiter_wheels.bike_usage_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Bike Usage Management component.
 * Exposes endpoints for managing bike usage lifecycle during rentals.
 */
@RestController
@RequestMapping("/api/bike-usage")
class BikeUsageManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeUsageManagementController::class.java)
    }

    /**
     * Pauses an active rental
     */
    @PostMapping("/{rentalId}/pause")
    fun pauseRental(@PathVariable rentalId: String): ResponseEntity<String> {
        val command = PauseRental(rentalId = rentalId)
        logger.info("Dispatching PauseRental command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Rental pause accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch PauseRental command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to pause rental")
        }
    }

    /**
     * Ends a rental early due to problems
     */
    @PostMapping("/{rentalId}/end-early")
    fun endRentalEarly(
        @PathVariable rentalId: String,
        @RequestBody request: EndRentalEarlyRequest
    ): ResponseEntity<String> {
        val command = EndRentalEarlyDueToProblem(
            problemDescription = request.problemDescription,
            rentalId = rentalId
        )
        logger.info("Dispatching EndRentalEarlyDueToProblem command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Early rental termination accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch EndRentalEarlyDueToProblem command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to end rental early")
        }
    }

    /**
     * Resumes a paused rental
     */
    @PostMapping("/{rentalId}/resume")
    fun resumeRental(@PathVariable rentalId: String): ResponseEntity<String> {
        val command = ResumeRental(rentalId = rentalId)
        logger.info("Dispatching ResumeRental command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Rental resume accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ResumeRental command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to resume rental")
        }
    }
}

/**
 * Request model for ending rental early
 */
data class EndRentalEarlyRequest(
    val problemDescription: String
)

