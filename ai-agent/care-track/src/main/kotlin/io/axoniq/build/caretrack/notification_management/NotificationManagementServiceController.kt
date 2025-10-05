package io.axoniq.build.caretrack.notification_management

import io.axoniq.build.caretrack.notification_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Notification Management Service component.
 * Exposes endpoints for creating and managing patient notifications and alerts.
 */
@RestController
@RequestMapping("/api/notifications")
class NotificationManagementServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(NotificationManagementServiceController::class.java)
    }

    /**
     * Creates an urgent health notification for a patient.
     */
    @PostMapping("/urgent-health")
    fun createUrgentHealthNotification(@RequestBody request: CreateUrgentHealthNotificationRequest): ResponseEntity<String> {
        val command = CreateUrgentHealthNotification(
            message = request.message,
            patientId = request.patientId,
            priority = request.priority
        )

        logger.info("Dispatching CreateUrgentHealthNotification command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Urgent health notification created")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateUrgentHealthNotification command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create urgent health notification")
        }
    }

    /**
     * Creates a missed appointment alert for a patient.
     */
    @PostMapping("/missed-appointment")
    fun createMissedAppointmentAlert(@RequestBody request: CreateMissedAppointmentAlertRequest): ResponseEntity<String> {
        val command = CreateMissedAppointmentAlert(
            patientId = request.patientId,
            appointmentId = request.appointmentId,
            alertMessage = request.alertMessage
        )
        
        logger.info("Dispatching CreateMissedAppointmentAlert command: $command")
        
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Missed appointment alert created")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateMissedAppointmentAlert command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create missed appointment alert")
        }
    }

    /**
     * Creates a treatment notification for a patient.
     */
    @PostMapping("/treatment")
    fun createTreatmentNotification(@RequestBody request: CreateTreatmentNotificationRequest): ResponseEntity<String> {
        val command = CreateTreatmentNotification(
            patientId = request.patientId,
            treatmentDetails = request.treatmentDetails
        )

        logger.info("Dispatching CreateTreatmentNotification command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Treatment notification created")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateTreatmentNotification command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create treatment notification")
        }
    }

    /**
     * Acknowledges an urgent notification by a family member.
     */
    @PostMapping("/acknowledge")
    fun acknowledgeUrgentNotification(@RequestBody request: AcknowledgeUrgentNotificationRequest): ResponseEntity<String> {
        val command = AcknowledgeUrgentNotification(
            familyMemberEmail = request.familyMemberEmail,
            notificationId = request.notificationId
        )

        logger.info("Dispatching AcknowledgeUrgentNotification command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Urgent notification acknowledged")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AcknowledgeUrgentNotification command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to acknowledge urgent notification")
        }
    }

    /**
     * Request data class for creating urgent health notifications.
     */
    data class CreateUrgentHealthNotificationRequest(
        val message: String,
        val patientId: String,
        val priority: String
    )

    /**
     * Request data class for creating missed appointment alerts.
     */
    data class CreateMissedAppointmentAlertRequest(
        val patientId: String,
        val appointmentId: String,
        val alertMessage: String
    )

    /**
     * Request data class for creating treatment notifications.
     */
    data class CreateTreatmentNotificationRequest(
        val patientId: String,
        val treatmentDetails: String
    )

    /**
     * Request data class for acknowledging urgent notifications.
     */
    data class AcknowledgeUrgentNotificationRequest(
        val familyMemberEmail: String,
        val notificationId: String
    )
}

