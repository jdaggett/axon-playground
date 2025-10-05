package io.axoniq.build.apex_racing_labs.driver_rating

import io.axoniq.build.apex_racing_labs.driver_rating.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Event-sourced state for the Driver Performance Rating Service component.
 * Maintains the current state based on driver rating events.
 */
@EventSourcedEntity
class DriverPerformanceRatingState {

    // Map to store user ratings: "userId:driverId:raceId" -> rating
    private val userRatings: MutableMap<String, Int> = mutableMapOf()

    /**
     * Creates an empty state instance for the driver performance rating service.
     */
    @EntityCreator
    constructor()

    /**
     * Checks if a user has already rated a specific driver for a specific race.
     *
     * @param userId The ID of the user
     * @param driverId The ID of the driver
     * @param raceId The ID of the race
     * @return True if the user has rated the driver for this race, false otherwise
     */
    fun hasUserRatedDriverInRace(userId: String, driverId: String, raceId: String): Boolean {
        val key = createRatingKey(userId, driverId, raceId)
        return userRatings.containsKey(key)
    }

    /**
     * Gets the rating that a user gave to a driver for a specific race.
     *
     * @param userId The ID of the user
     * @param driverId The ID of the driver
     * @param raceId The ID of the race
     * @return The rating given by the user, or 0 if no rating exists
     */
    fun getUserRatingForDriverInRace(userId: String, driverId: String, raceId: String): Int {
        val key = createRatingKey(userId, driverId, raceId)
        return userRatings[key] ?: 0
    }

    /**
     * Creates a unique key for storing user ratings.
     *
     * @param userId The ID of the user
     * @param driverId The ID of the driver
     * @param raceId The ID of the race
     * @return A unique key combining the three IDs
     */
    private fun createRatingKey(userId: String, driverId: String, raceId: String): String {
        return "$userId:$driverId:$raceId"
    }

    /**
     * Handles the DriverPerformanceRated event by storing the new rating in the state.
     * This method is called when a user rates a driver's performance for the first time.
     *
     * @param event The DriverPerformanceRated event containing rating information
     */
    @EventSourcingHandler
    fun evolve(event: DriverPerformanceRated) {
        val key = createRatingKey(event.userId, event.driverId, event.raceId)
        userRatings[key] = event.rating
    }

    /**
     * Handles the DriverRatingUpdated event by updating the existing rating in the state.
     * This method is called when a user updates their existing rating for a driver.
     *
     * @param event The DriverRatingUpdated event containing updated rating information
     */
    @EventSourcingHandler
    fun evolve(event: DriverRatingUpdated) {
        val key = createRatingKey(event.userId, event.driverId, event.raceId)
        userRatings[key] = event.newRating
    }
}