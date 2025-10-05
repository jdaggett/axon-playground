package io.axoniq.build.apex_racing_labs.user_setup

import io.axoniq.build.apex_racing_labs.user_setup.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity for User Setup Service component.
 * Maintains the state of user setup completion status and tracks whether
 * a user has completed their initial setup with preferences.
 */
@EventSourcedEntity
class UserSetupServiceState {

    private var userId: String? = null
    private var setupCompleted: Boolean = false

    fun getUserId(): String? = userId
    fun getSetupCompleted(): Boolean = setupCompleted

    @EntityCreator
    constructor()

    /**
     * Evolves the state when UserSetupCompleted event is processed.
     * Updates the user ID and marks the setup as completed.
     *
     * @param event The UserSetupCompleted event containing user setup information
     */
    @EventSourcingHandler
    fun evolve(event: UserSetupCompleted) {
        this.userId = event.userId
        this.setupCompleted = true
    }

    companion object {
        /**
         * Builds event criteria for loading User Setup Service events.
         * Filters events by user ID to reconstruct the user setup state.
         *
         * @param id The user ID to filter events for
         * @return EventCriteria for loading user setup events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("User", id))
                .andBeingOneOfTypes(
                    UserSetupCompleted::class.java.name
                )
        }
    }
}

