package io.axoniq.build.caretrack.family_health_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for permitted diagnoses.
 * Part of the Family Health View component for diagnosis data access operations.
 */
@Repository
interface PermittedDiagnosisRepository : JpaRepository<PermittedDiagnosis, Long> {

    /**
     * Find permitted diagnosis by diagnosis ID and permission.
     * Used by the Family Health View component to manage diagnosis permissions.
     */
    fun findByDiagnosisIdAndPermission(diagnosisId: String, permission: FamilyHealthPermission): PermittedDiagnosis?
}

