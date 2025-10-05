package io.axoniq.build.apex_racing_labs.race_management

import io.axoniq.build.apex_racing_labs.race_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.time.LocalDate

/**
 * Race Management State - Event Sourced Entity
 * Maintains the state of races through event sourcing for the race management component.
 */
@EventSourcedEntity
class RaceManagementState {
    private var raceId: String? = null
    private var status: String? = null
    private var raceDate: LocalDate? = null
    private var participatingDrivers: List<String>? = null
    private var trackName: String? = null

    // Getters for private properties
    fun getRaceId(): String? = raceId
    fun getStatus(): String? = status
    fun getRaceDate(): LocalDate? = raceDate
    fun getParticipatingDrivers(): List<String>? = participatingDrivers
    fun getTrackName(): String? = trackName

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for RaceCreated event.
     * Updates the state when a race is created.
     * 
     * @param event The RaceCreated event
     */
    @EventSourcingHandler
    fun evolve(event: RaceCreated) {
        this.raceId = event.raceId
        this.status = "CREATED"
        this.raceDate = event.raceDate
        this.participatingDrivers = event.participatingDriverIds
        this.trackName = event.trackName
    }

    /**
     * Event sourcing handler for RaceCancelled event.
     * Updates the state when a race is cancelled.
     * 
     * @param event The RaceCancelled event
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: RaceCancelled) {
        this.status = "CANCELLED"
    }

    companion object {
        /**
         * Event criteria builder for the Race Management component.
         * Defines which events should be loaded to reconstruct the state.
         *
         * @param id The race identifier
         * @return EventCriteria for loading race-related events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Race", id))
                .andBeingOneOfTypes(
                    RaceCreated::class.java.name,
                    RaceCancelled::class.java.name
                )
        }
    }
}

