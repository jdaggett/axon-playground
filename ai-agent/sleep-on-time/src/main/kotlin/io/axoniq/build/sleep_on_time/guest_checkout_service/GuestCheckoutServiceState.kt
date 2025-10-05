package io.axoniq.build.sleep_on_time.guest_checkout_service

import io.axoniq.build.sleep_on_time.guest_checkout_service.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Event sourced entity for the Guest Checkout Service component.
 * Maintains the state of guest checkout processes based on events.
 */
@EventSourcedEntity
class GuestCheckoutServiceState {
    
    private var bookingId: String? = null
    private var guestId: String? = null
    private var checkedInStatus: Boolean = false
    private var containerId: String? = null

    // Getters for accessing the state
    fun getBookingId(): String? = bookingId
    fun getGuestId(): String? = guestId
    fun getCheckedInStatus(): Boolean = checkedInStatus
    fun getContainerId(): String? = containerId

    @EntityCreator
    constructor()

    /**
     * Evolves the state when a GuestCheckedIn event is processed.
     * Updates the checked-in status and relevant identifiers.
     */
    @EventSourcingHandler
    fun evolve(event: GuestCheckedIn) {
        this.bookingId = event.bookingId
        this.guestId = event.guestId
        this.containerId = event.containerId
        this.checkedInStatus = true
    }

    /**
     * Evolves the state when a GuestCheckedOut event is processed.
     * Updates the checked-in status to false, indicating checkout completion.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: GuestCheckedOut) {
        this.checkedInStatus = false
    }

    /**
     * Evolves the state when a ContainerObtained event is processed.
     * Updates the container assignment information.
     */
    @EventSourcingHandler
    fun evolve(event: ContainerObtained) {
        this.bookingId = event.bookingId
        this.guestId = event.guestId
        this.containerId = event.containerId
    }

    companion object {
        /**
         * Builds the EventCriteria for loading events related to this guest checkout process.
         * Queries for events tagged with the specific booking, guest, and container identifiers.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: InitiateCheckOut.TargetIdentifier): EventCriteria {
            val bookingId = id.bookingId
            val guestId = id.guestId
            val containerId = id.containerId

            return EventCriteria.either(
                EventCriteria
                    .havingTags(Tag.of("Booking", bookingId))
                    .andBeingOneOfTypes(
                        GuestCheckedIn::class.java.name,
                        GuestCheckedOut::class.java.name,
                        ContainerObtained::class.java.name
                    ),
                EventCriteria
                    .havingTags(Tag.of("Guest", guestId))
                    .andBeingOneOfTypes(
                        GuestCheckedIn::class.java.name,
                        GuestCheckedOut::class.java.name,
                        ContainerObtained::class.java.name
                    ),
                EventCriteria
                    .havingTags(Tag.of("Container", containerId))
                    .andBeingOneOfTypes(
                        GuestCheckedIn::class.java.name,
                        GuestCheckedOut::class.java.name,
                        ContainerObtained::class.java.name
                    )
            )
        }
    }
}