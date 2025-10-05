package io.axoniq.build.apex_racing_labs.race_rating

import io.axoniq.build.apex_racing_labs.race_rating.api.*
import io.axoniq.build.apex_racing_labs.race_rating.exception.CannotRateCancelledRace
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Race Rating Service - Command handler for race rating operations.
 * This component handles user ratings for races and ensures business rules are enforced.
 */
class RaceRatingServiceCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceRatingServiceCommandHandler::class.java)
    }

    /**
     * Handles the RateRace command.
     * Validates that the race is not cancelled and processes the user rating.
     * 
     * @param command The RateRace command containing race ID, user ID, rating and optional comment
     * @param state The current state of the race rating entity
     * @param eventAppender Used to append the RaceRated event
     * @return RaceRatingResult indicating success or failure
     * @throws CannotRateCancelledRace if attempting to rate a cancelled race
     */
    @CommandHandler
    fun handle(
        command: RateRace,
        @InjectEntity state: RaceRatingState,
        eventAppender: EventAppender
    ): RaceRatingResult {
        logger.info("Processing RateRace command for race ID: ${command.raceId}, user ID: ${command.userId}")

        // Validate that the race is not cancelled
        if (state.getCancelled()) {
            logger.warn("Attempt to rate cancelled race: ${command.raceId}")
            throw CannotRateCancelledRace("Cannot rate a cancelled race")
        }

        // Create and append the RaceRated event
        val event = RaceRated(
            raceId = command.raceId,
            userId = command.userId,
            comment = command.comment,
            rating = command.rating
        )

        eventAppender.append(event)
        logger.info("Race rated successfully for race ID: ${command.raceId}, user ID: ${command.userId}, rating: ${command.rating}")

        return RaceRatingResult(
            success = true,
            message = "Race rated successfully"
        )
    }
}

