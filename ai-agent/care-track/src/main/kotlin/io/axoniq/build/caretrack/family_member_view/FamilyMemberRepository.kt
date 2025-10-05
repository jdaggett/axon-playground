package io.axoniq.build.caretrack.family_member_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for FamilyMemberEntity.
 * Provides data access methods for the Family Member View component.
 */
@Repository
interface FamilyMemberRepository : JpaRepository<FamilyMemberEntity, String> {

    /**
     * Finds all family members associated with a specific patient.
     */
    fun findByPatientId(patientId: String): List<FamilyMemberEntity>

    /**
     * Finds a specific family member by patient ID and email.
     */
    fun findByPatientIdAndFamilyMemberEmail(patientId: String, familyMemberEmail: String): FamilyMemberEntity?
}

