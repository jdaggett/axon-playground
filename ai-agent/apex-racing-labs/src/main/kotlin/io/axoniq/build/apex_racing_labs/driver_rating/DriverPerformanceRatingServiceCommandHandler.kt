package io.axoniq.build.apex_racing_labs.driver_rating

import io.axoniq.build.apex_racing_labs.driver_rating.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for the Driver Performance Rating Service component.
 * Handles user ratings for driver performance including rating and updating existing ratings.
 */
class DriverPerformanceRatingServiceCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverPerformanceRatingServiceCommandHandler::class.java)
    }

    /**
     * Handles the RateDriverPerformance command to allow users to rate a driver's performance in a race.
     * Creates a new rating for the specified driver.
     *
     * @param command The RateDriverPerformance command containing user, driver, race and rating information
     * @param state The current state of the driver performance rating service
     * @param eventAppender The event appender to publish domain events
     * @return DriverRatingResult indicating success or failure of the rating operation
     */
    @CommandHandler
    fun handle(
        command: RateDriverPerformance,
        @InjectEntity state: DriverPerformanceRatingState,
        eventAppender: EventAppender
    ): DriverRatingResult {
        logger.info("Handling RateDriverPerformance command for driver ${command.driverId} by user ${command.userId}")

        // Check if user has already rated this driver for this race
        if (state.hasUserRatedDriverInRace(command.userId, command.driverId, command.raceId)) {
            logger.warn("User ${command.userId} has already rated driver ${command.driverId} for race ${command.raceId}")
            return DriverRatingResult(
                success = false,
                message = "You have already rated this driver for this race"
            )
        }

        // Validate rating is within acceptable range (1-10)
        if (command.rating < 1 || command.rating > 10) {
            logger.warn("Invalid rating ${command.rating} provided by user ${command.userId}")
            return DriverRatingResult(
                success = false,
                message = "Rating must be between 1 and 10"
            )
        }

        // Create and publish the driver performance rated event
        val event = DriverPerformanceRated(
            userId = command.userId,
            driverId = command.driverId,
            raceId = command.raceId,
            rating = command.rating
        )

        eventAppender.append(event)
        logger.info("Successfully rated driver ${command.driverId} with rating ${command.rating}")

        return DriverRatingResult(
            success = true,
            message = "Driver performance rated successfully"
        )
    }

    /**
     * Handles the UpdateDriverRating command to allow users to update their existing rating for a driver.
     * Updates an existing rating for the specified driver.
     *
     * @param command The UpdateDriverRating command containing user, driver, race and new rating information
     * @param state The current state of the driver performance rating service
     * @param eventAppender The event appender to publish domain events
     * @return DriverRatingUpdateResult indicating success or failure of the update operation
     */
    @CommandHandler
    fun handle(
        command: UpdateDriverRating,
        @InjectEntity state: DriverPerformanceRatingState,
        eventAppender: EventAppender
    ): DriverRatingUpdateResult {
        logger.info("Handling UpdateDriverRating command for driver ${command.driverId} by user ${command.userId}")

        // Check if user has previously rated this driver for this race
        if (!state.hasUserRatedDriverInRace(command.userId, command.driverId, command.raceId)) {
            logger.warn("User ${command.userId} has not rated driver ${command.driverId} for race ${command.raceId}")
            return DriverRatingUpdateResult(
                success = false,
                message = "You have not rated this driver for this race yet"
            )
        }

        // Validate new rating is within acceptable range (1-10)
        if (command.newRating < 1 || command.newRating > 10) {
            logger.warn("Invalid rating ${command.newRating} provided by user ${command.userId}")
            return DriverRatingUpdateResult(
                success = false,
                message = "Rating must be between 1 and 10"
            )
        }

        // Get the previous rating
        val previousRating = state.getUserRatingForDriverInRace(command.userId, command.driverId, command.raceId)

        // Create and publish the driver rating updated event
        val event = DriverRatingUpdated(
            userId = command.userId,
            driverId = command.driverId,
            newRating = command.newRating,
            raceId = command.raceId,
            previousRating = previousRating
        )

        eventAppender.append(event)
        logger.info("Successfully updated driver ${command.driverId} rating from $previousRating to ${command.newRating}")

        return DriverRatingUpdateResult(
            success = true,
            message = "Driver rating updated successfully"
        )
    }
}

