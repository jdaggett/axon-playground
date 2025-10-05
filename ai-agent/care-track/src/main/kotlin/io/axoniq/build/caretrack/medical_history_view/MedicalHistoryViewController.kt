package io.axoniq.build.caretrack.medical_history_view

import io.axoniq.build.caretrack.medical_history_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Medical History View component.
 * Exposes HTTP endpoints for querying patient medical history and treatment information.
 */
@RestController
@RequestMapping("/api/medical-history")
class MedicalHistoryViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MedicalHistoryViewController::class.java)
    }

    /**
     * Retrieves detailed information about a specific treatment.
     * @param treatmentId The ID of the treatment to retrieve
     * @return Treatment details
     */
    @GetMapping("/treatments/{treatmentId}")
    fun getTreatmentDetails(@PathVariable treatmentId: String): CompletableFuture<TreatmentDetailsResult> {
        logger.info("REST request for treatment details: $treatmentId")
        val query = TreatmentDetails(treatmentId)
        return queryGateway.query(query, TreatmentDetailsResult::class.java, null)
    }

    /**
     * Retrieves the complete medical history for a patient.
     * @param patientId The ID of the patient
     * @return Patient's medical history including all diagnoses
     */
    @GetMapping("/patients/{patientId}/history")
    fun getPatientMedicalHistory(@PathVariable patientId: String): CompletableFuture<PatientMedicalHistoryResult> {
        logger.info("REST request for patient medical history: $patientId")
        val query = PatientMedicalHistory(patientId)
        return queryGateway.query(query, PatientMedicalHistoryResult::class.java, null)
    }
    
    /**
     * Retrieves all current active treatments for a patient.
     * @param patientId The ID of the patient
     * @return Patient's current treatments
     */
    @GetMapping("/patients/{patientId}/treatments")
    fun getPatientCurrentTreatments(@PathVariable patientId: String): CompletableFuture<PatientCurrentTreatmentsResult> {
        logger.info("REST request for patient current treatments: $patientId")
        val query = PatientCurrentTreatments(patientId)
        return queryGateway.query(query, PatientCurrentTreatmentsResult::class.java, null)
    }

    /**
     * Retrieves detailed information about a specific diagnosis.
     * @param diagnosisId The ID of the diagnosis to retrieve
     * @return Diagnosis details
     */
    @GetMapping("/diagnoses/{diagnosisId}")
    fun getDiagnosisDetails(@PathVariable diagnosisId: String): CompletableFuture<DiagnosisDetailsResult> {
        logger.info("REST request for diagnosis details: $diagnosisId")
        val query = DiagnosisDetails(diagnosisId)
        return queryGateway.query(query, DiagnosisDetailsResult::class.java, null)
    }
}