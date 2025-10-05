package io.axoniq.build.apex_racing_labs.user_preferences

import io.axoniq.build.apex_racing_labs.user_preferences.api.FavoriteTeamSelected
import io.axoniq.build.apex_racing_labs.user_preferences.api.FavoriteDriverSelected
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * UserPreferencesState - Event sourced entity for the User Preferences Service component.
 * Maintains the state of user preferences including favorite team and driver selections.
 */
@EventSourcedEntity
class UserPreferencesState {
    private var userId: String? = null
    private var favoriteTeamId: String? = null
    private var favoriteDriverId: String? = null

    /**
     * Gets the user ID for this preferences state
     */
    fun getUserId(): String? = userId

    /**
     * Gets the favorite team ID, may be null if not set
     */
    fun getFavoriteTeamId(): String? = favoriteTeamId

    /**
     * Gets the favorite driver ID, may be null if not set
     */
    fun getFavoriteDriverId(): String? = favoriteDriverId

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for FavoriteTeamSelected event.
     * Updates the state when a user selects a favorite team.
     */
    @EventSourcingHandler
    fun evolve(event: FavoriteTeamSelected) {
        this.userId = event.userId
        this.favoriteTeamId = event.teamId
    }

    /**
     * Event sourcing handler for FavoriteDriverSelected event.
     * Updates the state when a user selects a favorite driver.
     */
    @EventSourcingHandler
    fun evolve(event: FavoriteDriverSelected) {
        this.userId = event.userId
        this.favoriteDriverId = event.driverId
    }

    companion object {
        /**
         * Event criteria builder for loading user preferences events.
         * Loads all preference-related events for a specific user.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("User", id))
                .andBeingOneOfTypes(
                    FavoriteTeamSelected::class.java.name,
                    FavoriteDriverSelected::class.java.name
                )
        }
    }
}

