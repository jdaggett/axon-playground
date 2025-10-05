package io.axoniq.build.caretrack.family_health_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for permitted appointments.
 * Part of the Family Health View component for appointment data access operations.
 */
@Repository
interface PermittedAppointmentRepository : JpaRepository<PermittedAppointment, Long> {

    /**
     * Find permitted appointment by appointment ID and permission.
     * Used by the Family Health View component to manage appointment permissions.
     */
    fun findByAppointmentIdAndPermission(appointmentId: String, permission: FamilyHealthPermission): PermittedAppointment?
}

