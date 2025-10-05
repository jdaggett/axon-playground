package io.axoniq.build.apex_racing_labs.race_management

import io.axoniq.build.apex_racing_labs.race_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Race Management Service - Command Handler
 * Handles race creation and cancellation commands for the race management component.
 */
class RaceManagementCommandHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceManagementCommandHandler::class.java)
    }

    /**
     * Handles the CreateRace command to create a new race.
     * 
     * @param command The CreateRace command containing race details
     * @param state The current state of the race management component
     * @param eventAppender The event appender to publish events
     * @return RaceCreationResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: CreateRace,
        @InjectEntity state: RaceManagementState,
        eventAppender: EventAppender
    ): RaceCreationResult {
        logger.info("Handling CreateRace command for raceId: {}", command.raceId)

        // Validate that race doesn't already exist
        if (state.getRaceId() != null) {
            logger.warn("Race with id {} already exists", command.raceId)
            return RaceCreationResult(
                success = false,
                message = "Race with id ${command.raceId} already exists"
            )
        }

        // Create and append the RaceCreated event
        val event = RaceCreated(
            raceId = command.raceId,
            participatingDriverIds = command.participatingDriverIds,
            raceDate = command.raceDate,
            trackName = command.trackName
        )

        eventAppender.append(event)
        logger.info("Race created successfully with id: {}", command.raceId)

        return RaceCreationResult(
            success = true,
            message = "Race created successfully"
        )
    }

    /**
     * Handles the CancelRace command to cancel an existing race.
     * 
     * @param command The CancelRace command containing the race ID to cancel
     * @param state The current state of the race management component
     * @param eventAppender The event appender to publish events
     * @return RaceCancellationResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: CancelRace,
        @InjectEntity state: RaceManagementState,
        eventAppender: EventAppender
    ): RaceCancellationResult {
        logger.info("Handling CancelRace command for raceId: {}", command.raceId)

        // Validate that race exists
        if (state.getRaceId() == null) {
            logger.warn("Race with id {} does not exist", command.raceId)
            return RaceCancellationResult(
                success = false,
                message = "Race with id ${command.raceId} does not exist"
            )
        }

        // Validate that race is not already cancelled
        if (state.getStatus() == "CANCELLED") {
            logger.warn("Race with id {} is already cancelled", command.raceId)
            return RaceCancellationResult(
                success = false,
                message = "Race with id ${command.raceId} is already cancelled"
            )
        }

        // Create and append the RaceCancelled event
        val event = RaceCancelled(raceId = command.raceId)
        eventAppender.append(event)
        logger.info("Race cancelled successfully with id: {}", command.raceId)

        return RaceCancellationResult(
            success = true,
            message = "Race cancelled successfully"
        )
    }
}

