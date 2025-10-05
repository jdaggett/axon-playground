package io.axoniq.build.jupiter_wheels.bike_fleet_management

import io.axoniq.build.jupiter_wheels.bike_fleet_management.api.BikeCreated
import io.axoniq.build.jupiter_wheels.bike_fleet_management.api.BikeMarkedAsAvailable
import io.axoniq.build.jupiter_wheels.bike_fleet_management.api.BikeRemovedFromFleet
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Event-sourced entity for Bike Fleet Management component.
 * Maintains the state of a bike in the fleet based on historical events.
 */
@EventSourcedEntity
class BikeFleetManagementState {
    private var bikeId: String? = null
    private var location: String? = null
    private var bikeType: String? = null
    private var status: String? = null

    /**
     * Gets the bike ID
     */
    fun getBikeId(): String? = bikeId

    /**
     * Gets the bike location
     */
    fun getLocation(): String? = location

    /**
     * Gets the bike type
     */
    fun getBikeType(): String? = bikeType

    /**
     * Gets the bike status
     */
    fun getStatus(): String? = status

    /**
     * Default constructor for entity creation
     */
    @EntityCreator
    constructor()

    /**
     * Handles BikeCreated event to initialize bike state
     */
    @EventSourcingHandler
    fun evolve(event: BikeCreated) {
        this.bikeId = event.bikeId
        this.location = event.location
        this.bikeType = event.bikeType
        this.status = "CREATED"
    }

    /**
     * Handles BikeMarkedAsAvailable event to update bike status
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: BikeMarkedAsAvailable) {
        this.status = "AVAILABLE"
    }

    /**
     * Handles BikeRemovedFromFleet event to update bike status
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: BikeRemovedFromFleet) {
        this.status = "REMOVED"
    }

    companion object {
        /**
         * Builds event criteria to load events for a specific bike
         */
        @EventCriteriaBuilder
        fun resolveCriteria(bikeId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Bike", bikeId))
                .andBeingOneOfTypes(
                    BikeCreated::class.java.name,
                    BikeMarkedAsAvailable::class.java.name,
                    BikeRemovedFromFleet::class.java.name
                )
        }
    }
}

