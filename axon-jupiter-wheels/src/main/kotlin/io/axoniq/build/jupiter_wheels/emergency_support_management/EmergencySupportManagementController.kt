package io.axoniq.build.jupiter_wheels.emergency_support_management

import io.axoniq.build.jupiter_wheels.emergency_support_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * EmergencySupportManagementController - REST controller for emergency support operations
 * Provides endpoints for contacting emergency support and providing GPS location during rentals
 */
@RestController
@RequestMapping("/api/emergency-support")
class EmergencySupportManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(EmergencySupportManagementController::class.java)
    }

    /**
     * Endpoint for contacting emergency support
     * Accepts emergency support requests and dispatches ContactEmergencySupport command
     */
    @PostMapping("/contact")
    fun contactEmergencySupport(@RequestBody request: ContactEmergencySupportRequest): ResponseEntity<String> {
        val command = ContactEmergencySupport(
            emergencyType = request.emergencyType,
            rentalId = request.rentalId
        )
        logger.info("Dispatching ContactEmergencySupport command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Emergency support contact request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ContactEmergencySupport command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to contact emergency support")
        }
    }

    /**
     * Endpoint for providing GPS location to emergency support
     * Accepts GPS coordinates and dispatches ProvideGPSLocation command
     */
    @PostMapping("/gps-location")
    fun provideGPSLocation(@RequestBody request: ProvideGPSLocationRequest): ResponseEntity<String> {
        val command = ProvideGPSLocation(
            latitude = request.latitude,
            longitude = request.longitude,
            rentalId = request.rentalId
        )
        logger.info("Dispatching ProvideGPSLocation command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("GPS location provided successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ProvideGPSLocation command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to provide GPS location")
        }
    }

    /**
     * Request data class for contacting emergency support
     */
    data class ContactEmergencySupportRequest(
        val emergencyType: String,
        val rentalId: String
    )

    /**
     * Request data class for providing GPS location
     */
    data class ProvideGPSLocationRequest(
        val latitude: Double,
        val longitude: Double,
        val rentalId: String
    )
}

