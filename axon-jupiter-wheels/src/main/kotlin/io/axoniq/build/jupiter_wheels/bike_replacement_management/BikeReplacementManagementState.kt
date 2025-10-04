package io.axoniq.build.jupiter_wheels.bike_replacement_management

import io.axoniq.build.jupiter_wheels.bike_replacement_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity state for the Bike Replacement Management component.
 * Tracks the state of bike replacement requests and assignments.
 */
@EventSourcedEntity
class BikeReplacementManagementState {

    private var originalBikeId: String? = null
    private var rentalId: String? = null
    private var replacementBikeId: String? = null
    
    fun getOriginalBikeId(): String? = originalBikeId
    fun getRentalId(): String? = rentalId
    fun getReplacementBikeId(): String? = replacementBikeId

    @EntityCreator
    constructor()

    /**
     * Evolves state when a bike replacement is requested.
     * Captures the original bike and rental information.
     */
    @EventSourcingHandler
    fun evolve(event: BikeReplacementRequested) {
        this.originalBikeId = event.originalBikeId
        this.rentalId = event.rentalId
    }

    /**
     * Evolves state when a replacement bike is assigned.
     * Updates the replacement bike information.
     */
    @EventSourcingHandler
    fun evolve(event: ReplacementBikeAssigned) {
        this.replacementBikeId = event.replacementBikeId
    }

    companion object {
        /**
         * Builds event criteria for loading the bike replacement management state.
         * Uses the rental ID to identify relevant events.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(rentalId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Rental", rentalId))
                .andBeingOneOfTypes(
                    BikeReplacementRequested::class.java.name,
                    ReplacementBikeAssigned::class.java.name
                )
        }
    }
}

