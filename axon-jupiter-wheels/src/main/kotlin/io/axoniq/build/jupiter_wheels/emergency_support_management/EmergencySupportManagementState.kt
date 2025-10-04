package io.axoniq.build.jupiter_wheels.emergency_support_management

import io.axoniq.build.jupiter_wheels.emergency_support_management.api.*
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag

/**
 * EmergencySupportManagementState - Event-sourced entity that maintains the state for emergency support management
 * This entity tracks the state of support connections and requests for rental emergencies
 */
@EventSourcedEntity
class EmergencySupportManagementState {

    private var supportConnected: Boolean = false
    private var rentalId: String? = null
    private var supportRequested: Boolean = false

    /**
     * Gets the current support connection status
     */
    fun getSupportConnected(): Boolean = supportConnected

    /**
     * Gets the rental ID associated with this emergency support session
     */
    fun getRentalId(): String? = rentalId

    /**
     * Gets whether support has been requested for this rental
     */
    fun getSupportRequested(): Boolean = supportRequested

    @EntityCreator
    constructor()

    /**
     * Handles PaymentCompleted events to initialize the emergency support state for a rental
     * This establishes the rental context for potential emergency support requests
     */
    @EventSourcingHandler
    fun evolve(event: PaymentCompleted) {
        this.rentalId = event.rentalId
    }

    /**
     * Handles EmergencySupportContacted events to mark that emergency support has been requested
     * This updates the state to reflect that a support request has been initiated
     */
    @EventSourcingHandler
    fun evolve(event: EmergencySupportContacted) {
        this.supportRequested = true
        this.rentalId = event.rentalId
    }

    /**
     * Handles SupportConnectionEstablished events to mark that a support connection is active
     * This indicates that emergency support communication has been successfully established
     */
    @EventSourcingHandler
    fun evolve(event: SupportConnectionEstablished) {
        this.supportConnected = true
        this.rentalId = event.rentalId
    }

    companion object {
        /**
         * EventCriteriaBuilder for Emergency Support Management
         * Builds criteria to load events related to emergency support for a specific rental
         */
        @EventCriteriaBuilder
        fun resolveCriteria(rentalId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Rental", rentalId))
                .andBeingOneOfTypes(
                    PaymentCompleted::class.java.name,
                    EmergencySupportContacted::class.java.name,
                    SupportConnectionEstablished::class.java.name
                )
        }
    }
}

