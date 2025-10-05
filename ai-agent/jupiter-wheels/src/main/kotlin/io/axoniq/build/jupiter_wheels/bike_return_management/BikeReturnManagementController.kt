package io.axoniq.build.jupiter_wheels.bike_return_management

import io.axoniq.build.jupiter_wheels.bike_return_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for the Bike Return Management component.
 * Exposes endpoints for bike return process and validation.
 */
@RestController
@RequestMapping("/api/bike-return")
class BikeReturnManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeReturnManagementController::class.java)
    }

    /**
     * Endpoint to approve or reject a submitted photo.
     */
    @PostMapping("/{rentalId}/photo/approve")
    fun approveOrRejectPhoto(
        @PathVariable rentalId: String,
        @RequestParam approved: Boolean
    ): ResponseEntity<String> {
        val command = ApproveOrRejectPhoto(
            approved = approved,
            rentalId = rentalId
        )
        logger.info("Dispatching ApproveOrRejectPhoto command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Photo approval processed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ApproveOrRejectPhoto command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process photo approval")
        }
    }

    /**
     * Endpoint to report inspection results.
     */
    @PostMapping("/{rentalId}/inspection")
    fun reportInspectionResults(
        @PathVariable rentalId: String,
        @RequestParam inspectionPassed: Boolean,
        @RequestParam(required = false) issues: String?
    ): ResponseEntity<String> {
        val command = ReportInspectionResults(
            inspectionPassed = inspectionPassed,
            issues = issues,
            rentalId = rentalId
        )
        logger.info("Dispatching ReportInspectionResults command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Inspection results reported")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ReportInspectionResults command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report inspection results")
        }
    }

    /**
     * Endpoint to submit a bike photo.
     */
    @PostMapping("/{rentalId}/photo")
    fun submitBikePhoto(
        @PathVariable rentalId: String,
        @RequestParam photoUrl: String
    ): ResponseEntity<String> {
        val command = SubmitBikePhoto(
            photoUrl = photoUrl,
            rentalId = rentalId
        )
        logger.info("Dispatching SubmitBikePhoto command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Bike photo submitted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SubmitBikePhoto command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to submit bike photo")
        }
    }

    /**
     * Endpoint to submit a return survey.
     */
    @PostMapping("/{rentalId}/survey")
    fun submitReturnSurvey(
        @PathVariable rentalId: String,
        @RequestParam rating: Int,
        @RequestParam(required = false) feedback: String?
    ): ResponseEntity<String> {
        val command = SubmitReturnSurvey(
            feedback = feedback,
            rating = rating,
            rentalId = rentalId
        )
        logger.info("Dispatching SubmitReturnSurvey command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Return survey submitted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SubmitReturnSurvey command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to submit return survey")
        }
    }

    /**
     * Endpoint to return a bike at a specific location.
     */
    @PostMapping("/{rentalId}/return")
    fun returnBikeAtLocation(
        @PathVariable rentalId: String,
        @RequestParam returnLocation: String
    ): ResponseEntity<String> {
        val command = ReturnBikeAtLocation(
            returnLocation = returnLocation,
            rentalId = rentalId
        )
        logger.info("Dispatching ReturnBikeAtLocation command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Bike return confirmed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ReturnBikeAtLocation command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to return bike")
        }
    }
}

