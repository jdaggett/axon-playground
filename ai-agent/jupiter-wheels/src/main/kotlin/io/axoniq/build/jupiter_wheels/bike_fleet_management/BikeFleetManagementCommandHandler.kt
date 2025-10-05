package io.axoniq.build.jupiter_wheels.bike_fleet_management

import io.axoniq.build.jupiter_wheels.bike_fleet_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for Bike Fleet Management component.
 * Handles bike creation and removal from fleet operations.
 */
class BikeFleetManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeFleetManagementCommandHandler::class.java)
    }

    /**
     * Handles CreateNewBike command to create a new bike in the fleet.
     * Scenario: Given that the fleet needs a new bike, when the administrator creates new bike,
     * then bike is created and bike is marked as available.
     */
    @CommandHandler
    fun handle(command: CreateNewBike, eventAppender: EventAppender): BikeCreationResult {
        logger.info("Handling CreateNewBike command: $command")

        // Generate a unique bike ID
        val bikeId = UUID.randomUUID().toString()

        // Create bike creation event
        val bikeCreatedEvent = BikeCreated(
            bikeId = bikeId,
            location = command.location,
            bikeType = command.bikeType,
            condition = command.condition
        )

        // Create bike availability event
        val bikeMarkedAsAvailableEvent = BikeMarkedAsAvailable(
            bikeId = bikeId
        )

        // Append events to event stream
        eventAppender.append(bikeCreatedEvent)
        eventAppender.append(bikeMarkedAsAvailableEvent)

        logger.info("Successfully created bike with ID: $bikeId")
        return BikeCreationResult(bikeId = bikeId)
    }

    /**
     * Handles RemoveBikeFromFleet command to remove a bike from the fleet.
     * Scenario: Given that bike is created, when the administrator removes bike from fleet,
     * then bike is removed from fleet.
     */
    @CommandHandler
    fun handle(
        command: RemoveBikeFromFleet,
        @InjectEntity state: BikeFleetManagementState,
        eventAppender: EventAppender
    ): BikeRemovalResult {
        logger.info("Handling RemoveBikeFromFleet command: $command")

        // Validate that bike exists
        if (state.getBikeId() == null) {
            logger.error("Cannot remove bike - bike with ID ${command.bikeId} does not exist")
            throw IllegalStateException("Bike with given id does not exist")
        }

        // Validate that bike is not already removed
        if (state.getStatus() == "REMOVED") {
            logger.error("Cannot remove bike - bike with ID ${command.bikeId} is already removed")
            throw IllegalStateException("Bike is already removed from fleet")
        }

        // Create bike removal event
        val bikeRemovedEvent = BikeRemovedFromFleet(
            bikeId = command.bikeId,
            removalReason = command.removalReason
        )

        // Append event to event stream
        eventAppender.append(bikeRemovedEvent)

        logger.info("Successfully removed bike with ID: ${command.bikeId}")
        return BikeRemovalResult(removalConfirmed = true)
    }
}

