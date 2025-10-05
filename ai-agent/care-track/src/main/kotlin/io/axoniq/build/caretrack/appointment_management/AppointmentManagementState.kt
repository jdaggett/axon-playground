package io.axoniq.build.caretrack.appointment_management

import io.axoniq.build.caretrack.appointment_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import java.time.LocalDateTime

/**
 * Event-sourced entity for the Appointment Management Service component.
 * Maintains state for patient appointments and their status tracking.
 */
@EventSourcedEntity
class AppointmentManagementState {

    private var patientId: String? = null
    private val appointments: MutableList<Appointment> = mutableListOf()

    /**
     * Gets the patient ID for this appointment management state.
     */
    fun getPatientId(): String? = patientId

    /**
     * Gets the list of appointments for this patient.
     */
    fun getAppointments(): List<Appointment> = appointments.toList()

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for AppointmentScheduled event.
     * Creates a new appointment entry in the state.
     */
    @EventSourcingHandler
    fun evolve(event: AppointmentScheduled) {
        if (patientId == null) {
            patientId = event.patientId
        }

        appointments.add(
            Appointment(
                appointmentId = event.appointmentId,
                doctorId = event.doctorId,
                appointmentDate = event.appointmentDate,
                status = "SCHEDULED"
            )
        )
    }

    /**
     * Event sourcing handler for AppointmentMissed event.
     * Updates the appointment status to missed.
     */
    @EventSourcingHandler
    fun evolve(event: AppointmentMissed) {
        val appointment = appointments.find { it.appointmentId == event.appointmentId }
        appointment?.let {
            val index = appointments.indexOf(it)
            appointments[index] = it.copy(status = "MISSED")
        }
    }

    /**
     * Event sourcing handler for AppointmentAttended event.
     * Updates the appointment status to attended.
     */
    @EventSourcingHandler
    fun evolve(event: AppointmentAttended) {
        val appointment = appointments.find { it.appointmentId == event.appointmentId }
        appointment?.let {
            val index = appointments.indexOf(it)
            appointments[index] = it.copy(status = "ATTENDED")
        }
    }

    /**
     * Event sourcing handler for AppointmentCancelled event.
     * Updates the appointment status to cancelled.
     */
    @EventSourcingHandler
    fun evolve(event: AppointmentCancelled) {
        val appointment = appointments.find { it.appointmentId == event.appointmentId }
        appointment?.let {
            val index = appointments.indexOf(it)
            appointments[index] = it.copy(status = "CANCELLED")
        }
    }

    companion object {
        /**
         * Event criteria builder for the Appointment Management Service component.
         * Defines which events should be loaded to reconstruct the state.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(id: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Appointment", id))
                .andBeingOneOfTypes(
                    AppointmentScheduled::class.java.name,
                    AppointmentMissed::class.java.name,
                    AppointmentAttended::class.java.name,
                    AppointmentCancelled::class.java.name
                )
        }
    }

    /**
     * Inner data class representing an appointment.
     */
    data class Appointment(
        val appointmentId: String,
        val doctorId: String,
        val appointmentDate: LocalDateTime,
        val status: String
    )
}