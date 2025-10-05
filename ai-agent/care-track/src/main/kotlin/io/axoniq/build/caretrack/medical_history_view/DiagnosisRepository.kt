package io.axoniq.build.caretrack.medical_history_view

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for managing DiagnosisEntity persistence operations.
 * Used by the Medical History View component to access diagnosis data.
 */
interface DiagnosisRepository : JpaRepository<DiagnosisEntity, String> {

    /**
     * Finds all diagnoses for a specific patient.
     * @param patientId The ID of the patient
     * @return List of diagnoses for the patient
     */
    fun findByPatientIdOrderByDiagnosisDateDesc(patientId: String): List<DiagnosisEntity>
}

