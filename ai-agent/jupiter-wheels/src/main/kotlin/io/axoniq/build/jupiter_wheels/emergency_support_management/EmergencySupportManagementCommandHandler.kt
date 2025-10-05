package io.axoniq.build.jupiter_wheels.emergency_support_management

import io.axoniq.build.jupiter_wheels.emergency_support_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.modelling.annotations.InjectEntity
import org.axonframework.eventhandling.gateway.EventAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * EmergencySupportManagementCommandHandler - Handles commands related to emergency support management
 * This component processes emergency support requests and GPS location sharing for bike rentals
 */
class EmergencySupportManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(EmergencySupportManagementCommandHandler::class.java)
    }

    /**
     * Handles ContactEmergencySupport commands to initiate emergency support contact
     * Given that a bike is marked as in use, when the user contacts emergency support,
     * then emergency support is contacted
     */
    @CommandHandler
    fun handle(
        command: ContactEmergencySupport,
        @InjectEntity state: EmergencySupportManagementState,
        eventAppender: EventAppender
    ): EmergencySupportResult {
        logger.info("Processing ContactEmergencySupport command for rental: ${command.rentalId}")

        // Check if support has already been requested to avoid duplicate requests
        if (state.getSupportRequested()) {
            logger.warn("Emergency support already requested for rental: ${command.rentalId}")
        }

        // Generate a unique support contact ID for tracking this emergency request
        val supportContactId = UUID.randomUUID().toString()

        // Append the emergency support contacted event
        val event = EmergencySupportContacted(
            emergencyType = command.emergencyType,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        logger.info("Emergency support contacted for rental: ${command.rentalId}, emergency type: ${command.emergencyType}")

        return EmergencySupportResult(supportContactId = supportContactId)
    }

    /**
     * Handles ProvideGPSLocation commands to share GPS coordinates with emergency support
     * This enables location-based assistance by establishing a support connection
     */
    @CommandHandler
    fun handle(
        command: ProvideGPSLocation,
        @InjectEntity state: EmergencySupportManagementState,
        eventAppender: EventAppender
    ): GPSLocationResult {
        logger.info("Processing ProvideGPSLocation command for rental: ${command.rentalId}")

        // Validate that GPS coordinates are within valid ranges
        if (command.latitude < -90.0 || command.latitude > 90.0) {
            logger.error("Invalid latitude provided: ${command.latitude}")
            return GPSLocationResult(locationProvided = false)
        }

        if (command.longitude < -180.0 || command.longitude > 180.0) {
            logger.error("Invalid longitude provided: ${command.longitude}")
            return GPSLocationResult(locationProvided = false)
        }

        // Generate a support agent ID for the connection
        val supportAgentId = UUID.randomUUID().toString()

        // Establish support connection with the provided GPS location
        val event = SupportConnectionEstablished(
            supportAgentId = supportAgentId,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        logger.info("GPS location provided and support connection established for rental: ${command.rentalId}")
        
        return GPSLocationResult(locationProvided = true)
    }
}

