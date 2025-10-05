package io.axoniq.build.caretrack.appointment_management

import io.axoniq.build.caretrack.appointment_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Appointment Management Service component.
 * Handles patient appointment scheduling and tracking operations.
 */
class AppointmentManagementServiceCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppointmentManagementServiceCommandHandler::class.java)
    }

    /**
     * Handles the SchedulePatientAppointment command.
     * Creates a new appointment for the patient with the specified doctor and purpose.
     */
    @CommandHandler
    fun handle(
        command: SchedulePatientAppointment,
        @InjectEntity state: AppointmentManagementState,
        eventAppender: EventAppender
    ): AppointmentSchedulingResult {
        logger.info("Handling SchedulePatientAppointment command for patient ${command.patientId}")

        val appointmentId = UUID.randomUUID().toString()

        val event = AppointmentScheduled(
            patientId = command.patientId,
            doctorId = command.doctorId,
            purpose = command.purpose,
            appointmentDate = command.appointmentDate,
            appointmentId = appointmentId
        )

        eventAppender.append(event)
        logger.info("AppointmentScheduled event published for appointment $appointmentId")

        return AppointmentSchedulingResult(
            appointmentScheduled = true,
            appointmentId = appointmentId
        )
    }

    /**
     * Handles the MarkAppointmentMissed command.
     * Marks an existing appointment as missed.
     */
    @CommandHandler
    fun handle(
        command: MarkAppointmentMissed,
        @InjectEntity state: AppointmentManagementState,
        eventAppender: EventAppender
    ): AppointmentMissedResult {
        logger.info("Handling MarkAppointmentMissed command for appointment ${command.appointmentId}")

        val appointment = state.getAppointments().find { it.appointmentId == command.appointmentId }
        if (appointment == null) {
            logger.error("Appointment ${command.appointmentId} not found")
            throw IllegalStateException("Appointment with id ${command.appointmentId} not found")
        }
        
        if (appointment.status == "MISSED") {
            logger.warn("Appointment ${command.appointmentId} is already marked as missed")
            throw IllegalStateException("Appointment is already marked as missed")
        }

        if (appointment.status == "CANCELLED") {
            logger.error("Cannot mark cancelled appointment ${command.appointmentId} as missed")
            throw IllegalStateException("Cannot mark cancelled appointment as missed")
        }

        val event = AppointmentMissed(
            doctorId = command.doctorId,
            appointmentId = command.appointmentId
        )

        eventAppender.append(event)
        logger.info("AppointmentMissed event published for appointment ${command.appointmentId}")
        
        return AppointmentMissedResult(missedStatusMarked = true)
    }

    /**
     * Handles the MarkAppointmentAttended command.
     * Marks an existing appointment as attended.
     */
    @CommandHandler
    fun handle(
        command: MarkAppointmentAttended,
        @InjectEntity state: AppointmentManagementState,
        eventAppender: EventAppender
    ): AppointmentAttendanceResult {
        logger.info("Handling MarkAppointmentAttended command for appointment ${command.appointmentId}")
        
        val appointment = state.getAppointments().find { it.appointmentId == command.appointmentId }
        if (appointment == null) {
            logger.error("Appointment ${command.appointmentId} not found")
            throw IllegalStateException("Appointment with id ${command.appointmentId} not found")
        }
        
        if (appointment.status == "ATTENDED") {
            logger.warn("Appointment ${command.appointmentId} is already marked as attended")
            throw IllegalStateException("Appointment is already marked as attended")
        }

        if (appointment.status == "CANCELLED") {
            logger.error("Cannot mark cancelled appointment ${command.appointmentId} as attended")
            throw IllegalStateException("Cannot mark cancelled appointment as attended")
        }

        val event = AppointmentAttended(
            doctorId = command.doctorId,
            appointmentId = command.appointmentId
        )

        eventAppender.append(event)
        logger.info("AppointmentAttended event published for appointment ${command.appointmentId}")

        return AppointmentAttendanceResult(attendanceMarked = true)
    }

    /**
     * Handles the CancelPatientAppointment command.
     * Cancels an existing appointment for the patient.
     */
    @CommandHandler
    fun handle(
        command: CancelPatientAppointment,
        @InjectEntity state: AppointmentManagementState,
        eventAppender: EventAppender
    ): AppointmentCancellationResult {
        logger.info("Handling CancelPatientAppointment command for appointment ${command.appointmentId}")

        val appointment = state.getAppointments().find { it.appointmentId == command.appointmentId }
        if (appointment == null) {
            logger.error("Appointment ${command.appointmentId} not found")
            throw IllegalStateException("Appointment with id ${command.appointmentId} not found")
        }
        
        if (appointment.status == "CANCELLED") {
            logger.warn("Appointment ${command.appointmentId} is already cancelled")
            throw IllegalStateException("Appointment is already cancelled")
        }

        val event = AppointmentCancelled(
            doctorId = command.doctorId,
            cancellationReason = command.cancellationReason,
            appointmentId = command.appointmentId
        )

        eventAppender.append(event)
        logger.info("AppointmentCancelled event published for appointment ${command.appointmentId}")
        
        return AppointmentCancellationResult(appointmentCancelled = true)
    }
}

