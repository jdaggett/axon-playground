package io.axoniq.build.sleep_on_time.container_access

import io.axoniq.build.sleep_on_time.container_access.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.time.LocalDateTime

/**
 * Container Access State - Event-sourced entity for Container Access Management component
 * Manages the state related to container access operations including guest check-in/check-out,
 * container occupation status, and access key management.
 */
@EventSourcedEntity
class ContainerAccessState {
    
    private var guestId: String? = null
    private var accessKeyStatus: String = "INACTIVE"
    private var containerId: String? = null
    private var status: String = "AVAILABLE"
    private var checkedInAt: LocalDateTime? = null
    private var checkedOutAt: LocalDateTime? = null

    // Getters for command handlers to access current state
    fun getGuestId(): String? = guestId
    fun getAccessKeyStatus(): String = accessKeyStatus
    fun getContainerId(): String? = containerId
    fun getStatus(): String = status
    fun getCheckedInAt(): LocalDateTime? = checkedInAt
    fun getCheckedOutAt(): LocalDateTime? = checkedOutAt

    @EntityCreator
    constructor()

    /**
     * Handles GuestCheckedIn event - updates state when guest checks into container
     */
    @EventSourcingHandler
    fun evolve(event: GuestCheckedIn) {
        this.guestId = event.guestId
        this.containerId = event.containerId
        this.status = "OCCUPIED"
        this.checkedInAt = event.checkedInAt
        this.accessKeyStatus = "ACTIVE"
    }

    /**
     * Handles GuestCheckedOut event - updates state when guest checks out of container
     */
    @EventSourcingHandler
    fun evolve(event: GuestCheckedOut) {
        this.status = "AVAILABLE"
        this.checkedOutAt = event.timestamp
        this.accessKeyStatus = "INACTIVE"
    }

    /**
     * Handles DoorOpeningRequested event - records door opening request
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: DoorOpeningRequested) {
        // Door opening request recorded, no state change needed
    }
    
    /**
     * Handles ContainerObtained event - updates state when container is obtained
     */
    @EventSourcingHandler
    fun evolve(event: ContainerObtained) {
        this.guestId = event.guestId
        this.containerId = event.containerId
        this.status = "RESERVED"
    }

    companion object {
        /**
         * Event criteria builder for loading container access events
         * Loads events based on booking, guest, and container identifiers
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: OpenContainerDoor.TargetIdentifier): EventCriteria {
            val bookingId = id.bookingId
            val guestId = id.guestId
            val containerId = id.containerId
            
            return EventCriteria.either(
                EventCriteria
                    .havingTags(Tag.of("Booking", bookingId))
                    .andBeingOneOfTypes(
                        GuestCheckedIn::class.java.name,
                        GuestCheckedOut::class.java.name,
                        DoorOpeningRequested::class.java.name,
                        ContainerObtained::class.java.name
                    ),
                EventCriteria.either(
                    EventCriteria
                        .havingTags(Tag.of("Guest", guestId))
                        .andBeingOneOfTypes(
                            GuestCheckedIn::class.java.name,
                            GuestCheckedOut::class.java.name,
                            DoorOpeningRequested::class.java.name,
                            ContainerObtained::class.java.name
                        ),
                    EventCriteria
                        .havingTags(Tag.of("Container", containerId))
                        .andBeingOneOfTypes(
                            GuestCheckedIn::class.java.name,
                            GuestCheckedOut::class.java.name,
                            DoorOpeningRequested::class.java.name,
                            ContainerObtained::class.java.name
                        )
                )
            )
        }

        /**
         * Event criteria builder for ConfirmDoorUnlocked command
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: ConfirmDoorUnlocked.TargetIdentifier): EventCriteria {
            val bookingId = id.bookingId
            val guestId = id.guestId
            val containerId = id.containerId

            return EventCriteria.either(
                EventCriteria
                    .havingTags(Tag.of("Booking", bookingId))
                    .andBeingOneOfTypes(
                        GuestCheckedIn::class.java.name,
                        GuestCheckedOut::class.java.name,
                        DoorOpeningRequested::class.java.name,
                        ContainerObtained::class.java.name
                    ),
                EventCriteria.either(
                    EventCriteria
                        .havingTags(Tag.of("Guest", guestId))
                        .andBeingOneOfTypes(
                            GuestCheckedIn::class.java.name,
                            GuestCheckedOut::class.java.name,
                            DoorOpeningRequested::class.java.name,
                            ContainerObtained::class.java.name
                        ),
                    EventCriteria
                        .havingTags(Tag.of("Container", containerId))
                        .andBeingOneOfTypes(
                            GuestCheckedIn::class.java.name,
                            GuestCheckedOut::class.java.name,
                            DoorOpeningRequested::class.java.name,
                            ContainerObtained::class.java.name
                        )
                )
            )
        }
        
        /**
         * Event criteria builder for ObtainContainer command
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: ObtainContainer.TargetIdentifier): EventCriteria {
            val bookingId = id.bookingId
            val guestId = id.guestId
            val containerId = id.containerId

            return EventCriteria.either(
                EventCriteria
                    .havingTags(Tag.of("Booking", bookingId))
                    .andBeingOneOfTypes(
                        GuestCheckedIn::class.java.name,
                        GuestCheckedOut::class.java.name,
                        DoorOpeningRequested::class.java.name,
                        ContainerObtained::class.java.name
                    ),
                EventCriteria.either(
                    EventCriteria
                        .havingTags(Tag.of("Guest", guestId))
                        .andBeingOneOfTypes(
                            GuestCheckedIn::class.java.name,
                            GuestCheckedOut::class.java.name,
                            DoorOpeningRequested::class.java.name,
                            ContainerObtained::class.java.name
                        ),
                    EventCriteria
                        .havingTags(Tag.of("Container", containerId))
                        .andBeingOneOfTypes(
                            GuestCheckedIn::class.java.name,
                            GuestCheckedOut::class.java.name,
                            DoorOpeningRequested::class.java.name,
                            ContainerObtained::class.java.name
                        )
                )
            )
        }
    }
}

