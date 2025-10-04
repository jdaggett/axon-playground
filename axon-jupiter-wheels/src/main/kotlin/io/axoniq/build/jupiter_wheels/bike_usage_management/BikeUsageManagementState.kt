package io.axoniq.build.jupiter_wheels.bike_usage_management

import io.axoniq.build.jupiter_wheels.bike_usage_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.time.LocalDateTime

/**
 * Event sourced entity representing the state of bike usage management during rentals.
 * This component tracks the lifecycle of bike usage including pause/resume operations and early rental termination.
 */
@EventSourcedEntity
class BikeUsageManagementState {
    private var userId: String? = null
    private var pauseStartTime: LocalDateTime? = null
    private var usageStatus: String = "UNKNOWN"
    private var rentalId: String? = null
    private var bikeId: String? = null

    fun getUserId(): String? = userId
    fun getPauseStartTime(): LocalDateTime? = pauseStartTime
    fun getUsageStatus(): String = usageStatus
    fun getRentalId(): String? = rentalId
    fun getBikeId(): String? = bikeId

    @EntityCreator
    constructor()

    /**
     * Handles BikeMarkedAsInUse event - initializes bike usage state when rental begins
     */
    @EventSourcingHandler
    fun evolve(event: BikeMarkedAsInUse) {
        this.rentalId = event.rentalId
        this.bikeId = event.bikeId
        this.usageStatus = "IN_USE"
    }

    /**
     * Handles PaymentCompleted event - updates state when payment is processed
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PaymentCompleted) {
        this.usageStatus = "PAYMENT_COMPLETED"
    }

    /**
     * Handles RentalPaused event - updates state when rental is paused
     */
    @EventSourcingHandler
    fun evolve(event: RentalPaused) {
        this.pauseStartTime = event.pauseStartTime
        this.usageStatus = "PAUSED"
    }

    /**
     * Handles RentalEndedEarly event - updates state when rental ends early due to problems
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: RentalEndedEarly) {
        this.usageStatus = "ENDED_EARLY"
        this.pauseStartTime = null
    }

    /**
     * Handles RentalResumed event - updates state when rental is resumed after pause
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: RentalResumed) {
        this.usageStatus = "IN_USE"
        this.pauseStartTime = null
    }

    companion object {
        /**
         * Builds event criteria for loading bike usage management state based on rental ID
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Rental", id))
                .andBeingOneOfTypes(
                    BikeMarkedAsInUse::class.java.name,
                    PaymentCompleted::class.java.name,
                    RentalPaused::class.java.name,
                    RentalEndedEarly::class.java.name,
                    RentalResumed::class.java.name,
                    ExtraFeesChargedExtendedPause::class.java.name
                )
        }
    }
}

