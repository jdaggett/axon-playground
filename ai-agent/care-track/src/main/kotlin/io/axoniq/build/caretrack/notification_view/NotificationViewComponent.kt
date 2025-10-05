package io.axoniq.build.caretrack.notification_view

import io.axoniq.build.caretrack.notification_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Notification View Component - handles urgent health notifications and alerts queries.
 * This component maintains a read model for notifications by processing events
 * and providing query capabilities for the notification system.
 */
@Component
class NotificationViewComponent(
    private val notificationRepository: NotificationRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NotificationViewComponent::class.java)
    }

    /**
     * Handles UrgentNotificationDetails query to retrieve detailed information about a specific notification.
     * Part of the Notification View component's query handling capabilities.
     */
    @QueryHandler
    fun handle(query: UrgentNotificationDetails): UrgentNotificationDetailsResult {
        logger.info("Handling UrgentNotificationDetails query for notification ID: ${query.notificationId}")

        val notification = notificationRepository.findById(query.notificationId)
            .orElseThrow { IllegalArgumentException("Notification not found: ${query.notificationId}") }

        return UrgentNotificationDetailsResult(
            notificationId = notification.notificationId,
            acknowledged = notification.acknowledged,
            message = notification.message,
            patientName = notification.patientName ?: "",
            createdDate = notification.createdDate,
            priority = notification.priority ?: ""
        )
    }
    
    /**
     * Handles UrgentHealthNotifications query to retrieve urgent notifications for a patient.
     * Part of the Notification View component's query handling capabilities.
     */
    @QueryHandler
    fun handle(query: UrgentHealthNotifications): UrgentHealthNotificationsResult {
        logger.info("Handling UrgentHealthNotifications query for patient ID: ${query.patientId}")

        val notifications = notificationRepository.findByPatientIdAndPriority(query.patientId, "urgent")
        
        val notificationSummaries = notifications.map { notification ->
            NotificationSummary(
                notificationId = notification.notificationId,
                acknowledged = notification.acknowledged,
                message = notification.message,
                createdDate = notification.createdDate
            )
        }

        return UrgentHealthNotificationsResult(notifications = notificationSummaries)
    }

    /**
     * Handles TreatmentNotificationCreated event to update the notification read model.
     * Part of the Notification View component's event processing capabilities.
     */
    @EventHandler
    fun on(event: TreatmentNotificationCreated) {
        logger.info("Processing TreatmentNotificationCreated event for notification ID: ${event.notificationId}")

        val notification = NotificationEntity(
            notificationId = event.notificationId,
            patientId = event.patientId,
            acknowledged = false,
            message = event.treatmentDetails,
            createdDate = LocalDateTime.now(),
            notificationType = "treatment",
            priority = "normal"
        )

        notificationRepository.save(notification)
        logger.debug("Saved treatment notification: ${event.notificationId}")
    }

    /**
     * Handles UrgentHealthNotificationCreated event to update the notification read model.
     * Part of the Notification View component's event processing capabilities.
     */
    @EventHandler
    fun on(event: UrgentHealthNotificationCreated) {
        logger.info("Processing UrgentHealthNotificationCreated event for notification ID: ${event.notificationId}")

        val notification = NotificationEntity(
            notificationId = event.notificationId,
            patientId = event.patientId,
            acknowledged = false,
            message = event.message,
            createdDate = LocalDateTime.now(),
            notificationType = "urgent_health",
            priority = event.priority
        )

        notificationRepository.save(notification)
        logger.debug("Saved urgent health notification: ${event.notificationId}")
    }

    /**
     * Handles UrgentNotificationAcknowledged event to update notification acknowledgment status.
     * Part of the Notification View component's event processing capabilities.
     */
    @EventHandler
    fun on(event: UrgentNotificationAcknowledged) {
        logger.info("Processing UrgentNotificationAcknowledged event for notification ID: ${event.notificationId}")

        val existingNotification = notificationRepository.findById(event.notificationId)
        if (existingNotification.isPresent) {
            val notification = existingNotification.get()
            val updatedNotification = notification.copy(acknowledged = true)
            notificationRepository.save(updatedNotification)
            logger.debug("Updated notification acknowledgment: ${event.notificationId}")
        } else {
            logger.warn("Notification not found for acknowledgment: ${event.notificationId}")
        }
    }
    
    /**
     * Handles MissedAppointmentAlertCreated event to update the notification read model.
     * Part of the Notification View component's event processing capabilities.
     */
    @EventHandler
    fun on(event: MissedAppointmentAlertCreated) {
        logger.info("Processing MissedAppointmentAlertCreated event for alert ID: ${event.alertId}")

        val notification = NotificationEntity(
            notificationId = event.alertId,
            patientId = event.patientId,
            acknowledged = false,
            message = event.alertMessage,
            createdDate = LocalDateTime.now(),
            notificationType = "missed_appointment",
            priority = "high"
        )

        notificationRepository.save(notification)
        logger.debug("Saved missed appointment alert: ${event.alertId}")
    }
}

