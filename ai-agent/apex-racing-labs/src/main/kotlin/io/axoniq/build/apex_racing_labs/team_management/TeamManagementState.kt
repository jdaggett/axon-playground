package io.axoniq.build.apex_racing_labs.team_management

import io.axoniq.build.apex_racing_labs.team_management.api.TeamCreated
import io.axoniq.build.apex_racing_labs.team_management.api.TeamRemoved
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity state for Team Management Service component.
 * Maintains the state of a team based on TeamCreated and TeamRemoved events.
 */
@EventSourcedEntity
class TeamManagementState {
    private var teamId: String? = null
    private var active: Boolean = false
    private var teamName: String? = null

    fun getTeamId(): String? = teamId
    fun getActive(): Boolean = active
    fun getTeamName(): String? = teamName

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for TeamCreated event.
     * Updates the state when a team is created.
     */
    @EventSourcingHandler
    fun evolve(event: TeamCreated) {
        this.teamId = event.teamId
        this.teamName = event.teamName
        this.active = true
    }

    /**
     * Event sourcing handler for TeamRemoved event.
     * Updates the state when a team is removed.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: TeamRemoved) {
        this.active = false
    }

    companion object {
        /**
         * Builds event criteria for loading team-related events.
         * Filters events by team ID tag for Team Management Service component.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Team", id))
                .andBeingOneOfTypes(
                    TeamCreated::class.java.name,
                    TeamRemoved::class.java.name
                )
        }
    }
}

