package io.axoniq.build.caretrack.appointment_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

/**
 * Repository interface for appointment entities.
 * Provides data access methods for appointment view queries.
 */
interface AppointmentRepository : JpaRepository<AppointmentEntity, String> {

    /**
     * Finds today's appointments for a specific doctor.
     * @param doctorId The doctor's ID
     * @param startOfDay The start of the day
     * @param endOfDay The end of the day
     * @return List of appointments for today
     */
    @Query("SELECT a FROM AppointmentEntity a WHERE a.doctorId = :doctorId AND a.appointmentDate >= :startOfDay AND a.appointmentDate <= :endOfDay ORDER BY a.appointmentDate ASC")
    fun findTodaysAppointmentsByDoctorId(
        @Param("doctorId") doctorId: String,
        @Param("startOfDay") startOfDay: LocalDateTime,
        @Param("endOfDay") endOfDay: LocalDateTime
    ): List<AppointmentEntity>

    /**
     * Finds upcoming appointments for a specific patient.
     * @param patientId The patient's ID
     * @param now The current date and time
     * @return List of upcoming appointments
     */
    @Query("SELECT a FROM AppointmentEntity a WHERE a.patientId = :patientId AND a.appointmentDate > :now AND a.status != 'CANCELLED' ORDER BY a.appointmentDate ASC")
    fun findUpcomingAppointmentsByPatientId(
        @Param("patientId") patientId: String,
        @Param("now") now: LocalDateTime
    ): List<AppointmentEntity>
}

