package io.axoniq.build.caretrack.notification_service

import io.axoniq.build.caretrack.notification_service.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Notification Service Integration - External system component that handles notification delivery
 * to family members and patients through external communication channels.
 * 
 * This component listens to various healthcare events and triggers appropriate notifications
 * via external systems like email, SMS, or push notifications.
 */
@Component
class NotificationServiceIntegration(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(NotificationServiceIntegration::class.java)

    /**
     * Notify Family Members of Treatment Change
     * Handles TreatmentDiscontinued events to inform family members about treatment changes.
     */
    @EventHandler
    fun handle(event: TreatmentDiscontinued, processingContext: ProcessingContext) {
        logger.info("Notifying family members of treatment discontinuation for patient ${event.patientId}, treatment ${event.treatmentId}")
        
        val treatmentDetails = "Treatment ${event.treatmentId} has been discontinued. Reason: ${event.reason ?: "Not specified"}"
        val command = CreateTreatmentNotification(
            patientId = event.patientId,
            treatmentDetails = treatmentDetails
        )

        commandGateway.send(command, processingContext)
    }

    /**
     * Notify Family Members of Treatment
     * Handles TreatmentPrescribed events to inform family members about new treatments.
     */
    @EventHandler
    fun handle(event: TreatmentPrescribed, processingContext: ProcessingContext) {
        logger.info("Notifying family members of new treatment prescription for patient ${event.patientId}")

        val treatmentDetails = "New treatment prescribed: ${event.medicationName}. " +
                "Dosage: ${event.dosage}, Frequency: ${event.frequency}, Duration: ${event.duration}"
        val command = CreateTreatmentNotification(
            patientId = event.patientId,
            treatmentDetails = treatmentDetails
        )

        commandGateway.send(command, processingContext)
    }

    /**
     * Notify Removed Family Member
     * Handles FamilyMemberAccessRevoked events to inform family members about access revocation.
     */
    @EventHandler
    fun handle(event: FamilyMemberAccessRevoked, processingContext: ProcessingContext) {
        logger.info("Notifying family member ${event.familyMemberEmail} about access revocation for patient ${event.patientId}")

        // External system would send notification email to the revoked family member
        // This is a stub implementation that logs the action
        logger.info("External notification sent to ${event.familyMemberEmail} about access revocation")
    }

    /**
     * Send Invitation Email
     * Handles FamilyMemberInvitationSent events to send invitation emails to family members.
     */
    @EventHandler
    fun handle(event: FamilyMemberInvitationSent, processingContext: ProcessingContext) {
        logger.info("Sending invitation email to ${event.familyMemberEmail} for patient ${event.patientId}")

        // External system would send invitation email with access level information
        // This is a stub implementation that logs the action
        logger.info("Invitation email sent to ${event.familyMemberEmail} with access level: ${event.accessLevel}")
    }

    /**
     * Send Appointment Reminder
     * Handles AppointmentScheduled events to send appointment reminders.
     */
    @EventHandler
    fun handle(event: AppointmentScheduled, processingContext: ProcessingContext) {
        logger.info("Sending appointment reminder for appointment ${event.appointmentId} to patient ${event.patientId}")

        // External system would send appointment reminder notifications
        // This is a stub implementation that logs the action
        logger.info("Appointment reminder sent for ${event.appointmentDate} with doctor ${event.doctorId}")
    }

    /**
     * Notify Family Members of Missed Appointment
     * Handles AppointmentMissed events to alert family members about missed appointments.
     */
    @EventHandler
    fun handle(event: AppointmentMissed, processingContext: ProcessingContext) {
        logger.info("Notifying family members of missed appointment ${event.appointmentId}")

        // Note: We need to determine patientId from the appointment context
        // For now, using appointmentId as a placeholder since patientId is not in the event
        val command = CreateMissedAppointmentAlert(
            patientId = "patient-from-${event.appointmentId}", // This would need proper resolution
            appointmentId = event.appointmentId,
            alertMessage = "Appointment ${event.appointmentId} with doctor ${event.doctorId} was missed"
        )

        commandGateway.send(command, processingContext)
    }

    /**
     * Notify Family Members of Account Deletion
     * Handles PatientAccountDeleted events to inform family members about account deletion.
     */
    @EventHandler
    fun handle(event: PatientAccountDeleted, processingContext: ProcessingContext) {
        logger.info("Notifying family members of account deletion for patient ${event.patientId}")

        // External system would send notification to all family members about account deletion
        // This is a stub implementation that logs the action
        logger.info("Account deletion notification sent to family members of patient ${event.patientId}")
    }

    /**
     * Notify Patient and Family of Cancellation
     * Handles AppointmentCancelled events to inform patients and family members about cancellations.
     */
    @EventHandler
    fun handle(event: AppointmentCancelled, processingContext: ProcessingContext) {
        logger.info("Notifying patient and family of appointment cancellation ${event.appointmentId}")
        
        // External system would send cancellation notifications
        // This is a stub implementation that logs the action
        val reason = event.cancellationReason ?: "No reason provided"
        logger.info("Appointment cancellation notification sent for ${event.appointmentId}. Reason: $reason")
    }

    /**
     * Notify Family Members of Diagnosis
     * Handles PatientDiagnosisRecorded events to alert family members about new diagnoses.
     */
    @EventHandler
    fun handle(event: PatientDiagnosisRecorded, processingContext: ProcessingContext) {
        logger.info("Notifying family members of new diagnosis for patient ${event.patientId}")

        val priority = if (event.severity.lowercase() in listOf("critical", "severe", "high")) "HIGH" else "MEDIUM"
        val message = "New diagnosis recorded: ${event.condition} (Severity: ${event.severity}). " +
                "Date: ${event.diagnosisDate}. ${event.notes?.let { "Notes: $it" } ?: ""}"

        val command = CreateUrgentHealthNotification(
            patientId = event.patientId,
            message = message,
            priority = priority
        )

        commandGateway.send(command, processingContext)
    }
}