package io.axoniq.build.jupiter_wheels.rental_extension_management

import io.axoniq.build.jupiter_wheels.rental_extension_management.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import java.time.LocalDateTime

/**
 * Event-sourced entity for Rental Extension Management component.
 * Maintains the state of rental extension requests and processing.
 */
@EventSourcedEntity
class RentalExtensionManagementState {

    private var extensionRequested: Boolean = false
    private var originalEndTime: LocalDateTime? = null
    private var gracePeriodActive: Boolean = false
    private var rentalId: String? = null

    fun getExtensionRequested(): Boolean = extensionRequested
    fun getOriginalEndTime(): LocalDateTime? = originalEndTime
    fun getGracePeriodActive(): Boolean = gracePeriodActive
    fun getRentalId(): String? = rentalId

    @EntityCreator
    constructor()

    /**
     * Handles RentalExtensionRequested event - marks that an extension has been requested
     */
    @EventSourcingHandler
    fun evolve(event: RentalExtensionRequested) {
        this.rentalId = event.rentalId
        this.extensionRequested = true
    }

    /**
     * Handles RentalExtensionApproved event - processes the approval of extension request
     */
    @EventSourcingHandler
    fun evolve(event: RentalExtensionApproved) {
        this.originalEndTime = event.newEndTime}

    /**
     * Handles GracePeriodActivated event - activates grace period for the rental
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: GracePeriodActivated) {
        this.gracePeriodActive = true
    }

    /**
     * Handles PaymentCompleted event - processes payment completion for extension
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PaymentCompleted) {
        // Payment completed processing handled here if needed
    }

    companion object {
        /**
         * Builds event criteria for loading rental extension management state
         */
        @EventCriteriaBuilder
        fun resolveCriteria(rentalId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Rental", rentalId))
                .andBeingOneOfTypes(
                    RentalExtensionRequested::class.java.name,
                    RentalExtensionApproved::class.java.name,
                    GracePeriodActivated::class.java.name,
                    PaymentCompleted::class.java.name
                )
        }
    }
}