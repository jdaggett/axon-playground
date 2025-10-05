package io.axoniq.build.apex_racing_labs.race_rating

import io.axoniq.build.apex_racing_labs.race_rating.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Race Rating State - Event-sourced entity that maintains the state of race ratings.
 * This entity tracks user ratings for races and the race cancellation status.
 */
@EventSourcedEntity
class RaceRatingState {
    
    private var raceId: String? = null
    private var cancelled: Boolean = false
    private var userRatings: MutableList<UserRating> = mutableListOf()
    
    fun getRaceId(): String? = raceId
    fun getCancelled(): Boolean = cancelled
    fun getUserRatings(): List<UserRating> = userRatings.toList()

    @EntityCreator
    constructor()

    /**
     * Handles the RaceRated event to update the state with new user rating.
     * 
     * @param event The RaceRated event containing the rating information
     */
    @EventSourcingHandler
    fun evolve(event: RaceRated) {
        this.raceId = event.raceId

        // Add or update user rating
        val existingRatingIndex = userRatings.indexOfFirst { it.userId == event.userId }
        val newRating = UserRating(
            userId = event.userId,
            comment = event.comment,
            rating = event.rating
        )

        if (existingRatingIndex >= 0) {
            userRatings[existingRatingIndex] = newRating
        } else {
            userRatings.add(newRating)
        }
    }

    /**
     * Handles the RaceCancelled event to mark the race as cancelled.
     *
     * @param event The RaceCancelled event
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: RaceCancelled) {
        this.cancelled = true
    }

    companion object {
        /**
         * Builds the event criteria for loading race rating events.
         * 
         * @param raceId The race identifier
         * @return EventCriteria for loading race-related events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(raceId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Race", raceId))
                .andBeingOneOfTypes(
                    RaceRated::class.java.name,
                    RaceCancelled::class.java.name
                )
        }
    }

    /**
     * Data class representing a user rating for a race.
     * 
     * @param userId The ID of the user who provided the rating
     * @param comment Optional comment from the user
     * @param rating The numerical rating given by the user
     */
    data class UserRating(
        val userId: String,
        val comment: String?,
        val rating: Int
    )
}

