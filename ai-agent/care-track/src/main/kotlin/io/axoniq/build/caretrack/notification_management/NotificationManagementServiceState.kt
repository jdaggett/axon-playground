package io.axoniq.build.caretrack.notification_management

import io.axoniq.build.caretrack.notification_management.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Event sourced entity representing the state of the Notification Management Service.
 * This entity maintains the patient's notification history and acknowledgment status.
 */
@EventSourcedEntity
class NotificationManagementServiceState {
    
    private var patientId: String? = null
    private var notifications: MutableList<Notification> = mutableListOf()

    /**
     * Gets the patient ID for this notification management service instance.
     */
    fun getPatientId(): String? = patientId

    /**
     * Gets the list of notifications for the patient.
     */
    fun getNotifications(): List<Notification> = notifications.toList()

    /**
     * Constructor for creating a new notification management service state.
     */
    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for UrgentHealthNotificationCreated events.
     * Updates the state when an urgent health notification is created.
     */
    @EventSourcingHandler
    fun evolve(event: UrgentHealthNotificationCreated) {
        this.patientId = event.patientId
        
        val notification = Notification(
            notificationType = "URGENT_HEALTH",
            notificationId = event.notificationId,
            message = event.message,
            acknowledged = false
        )

        notifications.add(notification)
    }

    /**
     * Event sourcing handler for MissedAppointmentAlertCreated events.
     * Updates the state when a missed appointment alert is created.
     */
    @EventSourcingHandler
    fun evolve(event: MissedAppointmentAlertCreated) {
        this.patientId = event.patientId

        val notification = Notification(
            notificationType = "MISSED_APPOINTMENT",
            notificationId = event.alertId,
            message = event.alertMessage,
            acknowledged = false
        )

        notifications.add(notification)
    }

    /**
     * Event sourcing handler for TreatmentNotificationCreated events.
     * Updates the state when a treatment notification is created.
     */
    @EventSourcingHandler
    fun evolve(event: TreatmentNotificationCreated) {
        this.patientId = event.patientId

        val notification = Notification(
            notificationType = "TREATMENT",
            notificationId = event.notificationId,
            message = event.treatmentDetails,
            acknowledged = false
        )
        
        notifications.add(notification)
    }

    /**
     * Event sourcing handler for UrgentNotificationAcknowledged events.
     * Updates the state when an urgent notification is acknowledged.
     */
    @EventSourcingHandler
    fun evolve(event: UrgentNotificationAcknowledged) {
        val notification = notifications.find { it.notificationId == event.notificationId }
        notification?.let {
            val index = notifications.indexOf(it)
            notifications[index] = it.copy(acknowledged = true)
        }
    }

    /**
     * Event criteria builder for loading events related to this notification management service state.
     * Uses the patient ID to filter relevant events.
     */
    companion object {
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Notification", id))
                .andBeingOneOfTypes(
                    UrgentHealthNotificationCreated::class.java.name,
                    MissedAppointmentAlertCreated::class.java.name,
                    TreatmentNotificationCreated::class.java.name,
                    UrgentNotificationAcknowledged::class.java.name
                )
        }
    }

    /**
     * Inner data class representing a notification in the system.
     */
    data class Notification(
        val notificationType: String,
        val notificationId: String,
        val message: String,
        val acknowledged: Boolean
    )
}

