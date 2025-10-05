package io.axoniq.build.caretrack.family_health_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository for family health permissions.
 * Part of the Family Health View component for data access operations.
 */
@Repository
interface FamilyHealthPermissionRepository : JpaRepository<FamilyHealthPermission, Long> {

    /**
     * Find family health permission by patient ID and family member email.
     * Used by the Family Health View component to retrieve permitted health information.
     */
    fun findByPatientIdAndFamilyMemberEmail(patientId: String, familyMemberEmail: String): FamilyHealthPermission?
}

