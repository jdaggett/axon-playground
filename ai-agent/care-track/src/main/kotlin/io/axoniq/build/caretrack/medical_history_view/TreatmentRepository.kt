package io.axoniq.build.caretrack.medical_history_view

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for managing TreatmentEntity persistence operations.
 * Used by the Medical History View component to access treatment data.
 */
interface TreatmentRepository : JpaRepository<TreatmentEntity, String> {
    
    /**
     * Finds all active treatments for a specific patient.
     * @param patientId The ID of the patient
     * @param status The status of treatments to find
     * @return List of active treatments for the patient
     */
    fun findByPatientIdAndStatus(patientId: String, status: String): List<TreatmentEntity>
}

