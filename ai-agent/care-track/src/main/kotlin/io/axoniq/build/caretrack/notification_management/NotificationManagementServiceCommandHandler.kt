package io.axoniq.build.caretrack.notification_management

import io.axoniq.build.caretrack.notification_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Notification Management Service component.
 * This handler processes urgent health notifications and alerts for patient care tracking.
 */
class NotificationManagementServiceCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NotificationManagementServiceCommandHandler::class.java)
    }

    /**
     * Handles the CreateUrgentHealthNotification command.
     * Creates an urgent health notification for a patient and appends the corresponding event.
     */
    @CommandHandler
    fun handle(
        command: CreateUrgentHealthNotification,
        @InjectEntity state: NotificationManagementServiceState,
        eventAppender: EventAppender
    ): UrgentNotificationResult {
        logger.info("Handling CreateUrgentHealthNotification command for patient: ${command.patientId}")
        
        val notificationId = UUID.randomUUID().toString()

        val event = UrgentHealthNotificationCreated(
            patientId = command.patientId,
            message = command.message,
            notificationId = notificationId,
            priority = command.priority
        )

        eventAppender.append(event)

        return UrgentNotificationResult(
            notificationId = notificationId,
            notificationCreated = true
        )
    }

    /**
     * Handles the CreateMissedAppointmentAlert command.
     * Creates an alert for missed appointments and appends the corresponding event.
     */
    @CommandHandler
    fun handle(
        command: CreateMissedAppointmentAlert,
        @InjectEntity state: NotificationManagementServiceState,
        eventAppender: EventAppender
    ): MissedAppointmentAlertResult {
        logger.info("Handling CreateMissedAppointmentAlert command for patient: ${command.patientId}")

        val alertId = UUID.randomUUID().toString()

        val event = MissedAppointmentAlertCreated(
            patientId = command.patientId,
            alertId = alertId,
            appointmentId = command.appointmentId,
            alertMessage = command.alertMessage
        )

        eventAppender.append(event)

        return MissedAppointmentAlertResult(
            alertId = alertId,
            alertCreated = true
        )
    }

    /**
     * Handles the CreateTreatmentNotification command.
     * Creates a treatment notification for a patient and appends the corresponding event.
     */
    @CommandHandler
    fun handle(
        command: CreateTreatmentNotification,
        @InjectEntity state: NotificationManagementServiceState,
        eventAppender: EventAppender
    ): TreatmentNotificationResult {
        logger.info("Handling CreateTreatmentNotification command for patient: ${command.patientId}")

        val notificationId = UUID.randomUUID().toString()

        val event = TreatmentNotificationCreated(
            patientId = command.patientId,
            notificationId = notificationId,
            treatmentDetails = command.treatmentDetails
        )
        
        eventAppender.append(event)

        return TreatmentNotificationResult(
            notificationId = notificationId,
            notificationCreated = true
        )
    }

    /**
     * Handles the AcknowledgeUrgentNotification command.
     * Acknowledges an urgent notification by a family member and appends the corresponding event.
     */
    @CommandHandler
    fun handle(
        command: AcknowledgeUrgentNotification,
        @InjectEntity state: NotificationManagementServiceState,
        eventAppender: EventAppender
    ): NotificationAcknowledgmentResult {
        logger.info("Handling AcknowledgeUrgentNotification command for notification: ${command.notificationId}")

        val event = UrgentNotificationAcknowledged(
            familyMemberEmail = command.familyMemberEmail,
            notificationId = command.notificationId
        )

        eventAppender.append(event)

        return NotificationAcknowledgmentResult(
            acknowledged = true
        )
    }
}

