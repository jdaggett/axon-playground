package io.axoniq.build.caretrack.appointment_view

import io.axoniq.build.caretrack.appointment_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

/**
 * Query component for appointment view.
 * Handles appointment-related queries and maintains the appointment read model.
 */
@Component
class AppointmentQueryComponent(
    private val appointmentRepository: AppointmentRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppointmentQueryComponent::class.java)
    }

    /**
     * Handles query for today's appointments for a specific doctor.
     * @param query The TodaysAppointments query containing doctorId
     * @return TodaysAppointmentsResult containing list of today's appointments
     */
    @QueryHandler
    fun handle(query: TodaysAppointments): TodaysAppointmentsResult {
        logger.info("Handling TodaysAppointments query for doctor: ${query.doctorId}")

        val today = LocalDate.now()
        val startOfDay = today.atStartOfDay()
        val endOfDay = today.atTime(LocalTime.MAX)

        val appointments = appointmentRepository.findTodaysAppointmentsByDoctorId(
            query.doctorId,
            startOfDay, 
            endOfDay
        )

        val appointmentInfos = appointments.map { appointment ->
            TodayAppointmentInfo(
                purpose = appointment.purpose,
                patientName = appointment.patientName ?: "Unknown",
                appointmentTime = appointment.appointmentDate,
                appointmentId = appointment.appointmentId
            )
        }

        logger.debug("Found ${appointmentInfos.size} appointments for doctor ${query.doctorId} today")
        return TodaysAppointmentsResult(appointmentInfos)
    }

    /**
     * Handles query for detailed appointment information.
     * @param query The AppointmentDetails query containing appointmentId
     * @return AppointmentDetailsResult containing detailed appointment information
     */
    @QueryHandler
    fun handle(query: AppointmentDetails): AppointmentDetailsResult {
        logger.info("Handling AppointmentDetails query for appointment: ${query.appointmentId}")

        val appointment = appointmentRepository.findById(query.appointmentId)
            .orElseThrow { IllegalArgumentException("Appointment not found: ${query.appointmentId}") }

        return AppointmentDetailsResult(
            doctorName = appointment.doctorName ?: "Unknown",
            purpose = appointment.purpose,
            patientName = appointment.patientName ?: "Unknown",
            appointmentDate = appointment.appointmentDate,
            status = appointment.status,
            appointmentId = appointment.appointmentId
        )
    }

    /**
     * Handles query for upcoming appointments for a specific patient.
     * @param query The PatientUpcomingAppointments query containing patientId
     * @return PatientUpcomingAppointmentsResult containing list of upcoming appointments
     */
    @QueryHandler
    fun handle(query: PatientUpcomingAppointments): PatientUpcomingAppointmentsResult {
        logger.info("Handling PatientUpcomingAppointments query for patient: ${query.patientId}")

        val now = LocalDateTime.now()
        val appointments = appointmentRepository.findUpcomingAppointmentsByPatientId(query.patientId, now)
        
        val appointmentInfos = appointments.map { appointment ->
            AppointmentInfo(
                doctorName = appointment.doctorName ?: "Unknown",
                purpose = appointment.purpose,
                appointmentDate = appointment.appointmentDate,
                appointmentId = appointment.appointmentId
            )
        }

        logger.debug("Found ${appointmentInfos.size} upcoming appointments for patient ${query.patientId}")
        return PatientUpcomingAppointmentsResult(appointmentInfos)
    }

    /**
     * Handles AppointmentScheduled event to create new appointment entry in the view.
     * @param event The AppointmentScheduled event
     */
    @EventHandler
    fun on(event: AppointmentScheduled) {
        logger.info("Handling AppointmentScheduled event for appointment: ${event.appointmentId}")

        val appointment = AppointmentEntity(
            appointmentId = event.appointmentId,
            patientId = event.patientId,
            doctorId = event.doctorId,
            purpose = event.purpose,
            appointmentDate = event.appointmentDate,
            status = "SCHEDULED"
        )

        appointmentRepository.save(appointment)
        logger.debug("Created appointment entry for: ${event.appointmentId}")
    }

    /**
     * Handles AppointmentCancelled event to update appointment status in the view.
     * @param event The AppointmentCancelled event
     */
    @EventHandler
    fun on(event: AppointmentCancelled) {
        logger.info("Handling AppointmentCancelled event for appointment: ${event.appointmentId}")

        val appointment = appointmentRepository.findById(event.appointmentId)
            .orElseThrow { IllegalArgumentException("Appointment not found: ${event.appointmentId}") }

        val updatedAppointment = appointment.copy(status = "CANCELLED")
        appointmentRepository.save(updatedAppointment)
        logger.debug("Updated appointment ${event.appointmentId} status to CANCELLED")
    }

    /**
     * Handles AppointmentAttended event to update appointment status in the view.
     * @param event The AppointmentAttended event
     */
    @EventHandler
    fun on(event: AppointmentAttended) {
        logger.info("Handling AppointmentAttended event for appointment: ${event.appointmentId}")

        val appointment = appointmentRepository.findById(event.appointmentId)
            .orElseThrow { IllegalArgumentException("Appointment not found: ${event.appointmentId}") }
        
        val updatedAppointment = appointment.copy(status = "ATTENDED")
        appointmentRepository.save(updatedAppointment)
        logger.debug("Updated appointment ${event.appointmentId} status to ATTENDED")
    }

    /**
     * Handles AppointmentMissed event to update appointment status in the view.
     * @param event The AppointmentMissed event
     */
    @EventHandler
    fun on(event: AppointmentMissed) {
        logger.info("Handling AppointmentMissed event for appointment: ${event.appointmentId}")

        val appointment = appointmentRepository.findById(event.appointmentId)
            .orElseThrow { IllegalArgumentException("Appointment not found: ${event.appointmentId}") }

        val updatedAppointment = appointment.copy(status = "MISSED")
        appointmentRepository.save(updatedAppointment)
        logger.debug("Updated appointment ${event.appointmentId} status to MISSED")
    }
}

