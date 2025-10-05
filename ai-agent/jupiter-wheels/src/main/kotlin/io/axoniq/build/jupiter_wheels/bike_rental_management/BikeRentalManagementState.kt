package io.axoniq.build.jupiter_wheels.bike_rental_management

import io.axoniq.build.jupiter_wheels.bike_rental_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * State entity for the Bike Rental Management component
 * Maintains the state of bike rentals and bike availability
 */
@EventSourcedEntity
class BikeRentalManagementState {
    private var rentalId: String? = null
    private var status: String = ""
    private var userId: String? = null
    private var bikeId: String = ""
    
    private var bikeAvailable: Boolean = false
    private var bikeRemoved: Boolean = false
    private var rentalRequestActive: Boolean = false

    fun getRentalId(): String? = rentalId
    fun getStatus(): String = status
    fun getUserId(): String? = userId
    fun getBikeId(): String = bikeId
    fun getBikeAvailable(): Boolean = bikeAvailable
    fun getBikeRemoved(): Boolean = bikeRemoved
    fun getRentalRequestActive(): Boolean = rentalRequestActive

    @EntityCreator
    constructor()

    /**
     * Evolves state when a bike rental is requested
     */
    @EventSourcingHandler
    fun evolve(event: BikeRentalRequested) {
        this.rentalId = event.rentalId
        this.userId = event.userId
        this.bikeId = event.bikeId
        this.status = "REQUESTED"
        this.rentalRequestActive = true
    }

    /**
     * Evolves state when a bike is created and becomes available
     */
    @EventSourcingHandler
    fun evolve(event: BikeCreated) {
        this.bikeId = event.bikeId
        this.bikeAvailable = true
        this.status = "AVAILABLE"
    }

    /**
     * Evolves state when a rental request is rejected due to timeout
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: RentalRequestRejectedTimeout) {
        this.status = "REJECTED_TIMEOUT"
        this.rentalRequestActive = false
        this.bikeAvailable = true
    }

    /**
     * Evolves state when a rental request is rejected due to cancellation
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: RentalRequestRejectedCancellation) {
        this.status = "REJECTED_CANCELLED"
        this.rentalRequestActive = false
        this.bikeAvailable = true
    }

    /**
     * Evolves state when a bike is removed from the fleet
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: BikeRemovedFromFleet) {
        this.bikeAvailable = false
        this.bikeRemoved = true
        this.status = "REMOVED"
    }

    companion object {
        /**
         * Builds EventCriteria to load events for this component state
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: RequestBikeRental.TargetIdentifier): EventCriteria {
            val userId = id.userId
            val bikeId = id.bikeId

            return EventCriteria.either(
                EventCriteria
                    .havingTags(Tag.of("Bike", bikeId))
                    .andBeingOneOfTypes(
                        BikeCreated::class.java.name,
                        BikeRemovedFromFleet::class.java.name
                    ),
                EventCriteria
                    .havingTags(Tag.of("Rental", userId))
                    .andBeingOneOfTypes(
                        BikeRentalRequested::class.java.name,
                        RentalRequestRejectedTimeout::class.java.name,
                        RentalRequestRejectedCancellation::class.java.name
                    )
            )
        }
    }
}