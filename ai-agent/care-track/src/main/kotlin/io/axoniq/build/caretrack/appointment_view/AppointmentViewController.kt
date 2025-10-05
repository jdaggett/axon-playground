package io.axoniq.build.caretrack.appointment_view

import io.axoniq.build.caretrack.appointment_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for appointment view endpoints.
 * Exposes appointment query functionality through HTTP API.
 */
@RestController
@RequestMapping("/api/appointments")
class AppointmentViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppointmentViewController::class.java)
    }

    /**
     * Gets today's appointments for a specific doctor.
     * @param doctorId The doctor's ID
     * @return CompletableFuture containing today's appointments result
     */
    @GetMapping("/today/{doctorId}")
    fun getTodaysAppointments(@PathVariable doctorId: String): CompletableFuture<TodaysAppointmentsResult> {
        logger.info("REST request for today's appointments for doctor: $doctorId")
        val query = TodaysAppointments(doctorId)
        return queryGateway.query(query, TodaysAppointmentsResult::class.java, null)
    }

    /**
     * Gets detailed information about a specific appointment.
     * @param appointmentId The appointment's ID
     * @return CompletableFuture containing appointment details result
     */
    @GetMapping("/{appointmentId}")
    fun getAppointmentDetails(@PathVariable appointmentId: String): CompletableFuture<AppointmentDetailsResult> {
        logger.info("REST request for appointment details: $appointmentId")
        val query = AppointmentDetails(appointmentId)
        return queryGateway.query(query, AppointmentDetailsResult::class.java, null)
    }

    /**
     * Gets upcoming appointments for a specific patient.
     * @param patientId The patient's ID
     * @return CompletableFuture containing patient's upcoming appointments result
     */
    @GetMapping("/patient/{patientId}/upcoming")
    fun getPatientUpcomingAppointments(@PathVariable patientId: String): CompletableFuture<PatientUpcomingAppointmentsResult> {
        logger.info("REST request for upcoming appointments for patient: $patientId")
        val query = PatientUpcomingAppointments(patientId)
        return queryGateway.query(query, PatientUpcomingAppointmentsResult::class.java, null)
    }
}