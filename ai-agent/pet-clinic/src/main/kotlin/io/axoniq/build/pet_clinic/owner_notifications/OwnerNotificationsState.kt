package io.axoniq.build.pet_clinic.owner_notifications

import io.axoniq.build.pet_clinic.owner_notifications.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventstreaming.Tag

/**
 * Owner Notifications - Event sourced entity that maintains state for owner notification processes
 * This class manages the in-memory model for owner notifications based on events
 */
@EventSourcedEntity
class OwnerNotificationsState {

    private var ownerEmail: String? = null
    private var petId: String? = null
    private var notificationStatus: String? = null

    /**
     * Gets the owner's email address
     */
    fun getOwnerEmail(): String? = ownerEmail

    /**
     * Gets the pet ID associated with the notification
     */
    fun getPetId(): String? = petId

    /**
     * Gets the current notification status
     */
    fun getNotificationStatus(): String? = notificationStatus

    /**
     * Entity creator constructor for Owner Notifications
     */
    @EntityCreator
    constructor()

    /**
     * Owner Notifications - Event sourcing handler for OwnerNotified event
     * Updates the state when an owner has been notified
     */
    @EventSourcingHandler
    fun evolve(event: OwnerNotified) {
        this.ownerEmail = event.ownerEmail
        this.petId = event.petId
        this.notificationStatus = event.notificationStatus
    }

    companion object {
        /**
         * Owner Notifications - Event criteria builder for loading notification events
         * Defines which events should be loaded to reconstruct the notification state
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("petId", id))
                .andBeingOneOfTypes(
                    OwnerNotified::class.java.name
                )
        }
    }
}