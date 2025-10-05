package io.axoniq.build.caretrack.family_health_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for permitted treatments.
 * Part of the Family Health View component for treatment data access operations.
 */
@Repository
interface PermittedTreatmentRepository : JpaRepository<PermittedTreatment, Long> {

    /**
     * Find permitted treatment by treatment ID and permission.
     * Used by the Family Health View component to manage treatment permissions.
     */
    fun findByTreatmentIdAndPermission(treatmentId: String, permission: FamilyHealthPermission): PermittedTreatment?
}

