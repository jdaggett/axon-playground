package io.axoniq.build.caretrack.medical_history_view

import io.axoniq.build.caretrack.medical_history_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Medical History View component that handles patient medical history and current treatments queries.
 * This component maintains a read model of patient diagnoses and treatments by listening to medical events
 * and provides query handlers for retrieving medical history information.
 */
@Component
class MedicalHistoryViewComponent(
    private val diagnosisRepository: DiagnosisRepository,
    private val treatmentRepository: TreatmentRepository
) {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MedicalHistoryViewComponent::class.java)
    }

    /**
     * Handles TreatmentDetails query to retrieve detailed information about a specific treatment.
     * @param query The TreatmentDetails query containing the treatment ID
     * @return TreatmentDetailsResult with treatment information
     */
    @QueryHandler
    fun handle(query: TreatmentDetails): TreatmentDetailsResult? {
        logger.info("Processing TreatmentDetails query for treatment ID: ${query.treatmentId}")

        val treatment = treatmentRepository.findById(query.treatmentId).orElse(null)
            ?: return null

        // Note: We don't have doctor name in the entity, using doctorId as placeholder
        return TreatmentDetailsResult(
            treatmentId = treatment.treatmentId,
            medicationName = treatment.medicationName,
            dosage = treatment.dosage,
            frequency = treatment.frequency,
            duration = treatment.duration,
            prescribingDoctorName = treatment.doctorId // Using doctorId as placeholder for doctor name
        )
    }

    /**
     * Handles PatientMedicalHistory query to retrieve all diagnoses for a patient.
     * @param query The PatientMedicalHistory query containing the patient ID
     * @return PatientMedicalHistoryResult with list of patient diagnoses
     */
    @QueryHandler
    fun handle(query: PatientMedicalHistory): PatientMedicalHistoryResult {
        logger.info("Processing PatientMedicalHistory query for patient ID: ${query.patientId}")

        val diagnoses = diagnosisRepository.findByPatientIdOrderByDiagnosisDateDesc(query.patientId)

        val diagnosisInfoList = diagnoses.map { diagnosis ->
            DiagnosisInfo(
                diagnosisId = diagnosis.diagnosisId,
                condition = diagnosis.condition,
                severity = diagnosis.severity,
                diagnosisDate = diagnosis.diagnosisDate
            )
        }

        return PatientMedicalHistoryResult(diagnoses = diagnosisInfoList)
    }

    /**
     * Handles PatientCurrentTreatments query to retrieve all active treatments for a patient.
     * @param query The PatientCurrentTreatments query containing the patient ID
     * @return PatientCurrentTreatmentsResult with list of active treatments
     */
    @QueryHandler
    fun handle(query: PatientCurrentTreatments): PatientCurrentTreatmentsResult {
        logger.info("Processing PatientCurrentTreatments query for patient ID: ${query.patientId}")

        val treatments = treatmentRepository.findByPatientIdAndStatus(query.patientId, "ACTIVE")

        val treatmentInfoList = treatments.map { treatment ->
            TreatmentInfo(
                treatmentId = treatment.treatmentId,
                medicationName = treatment.medicationName,
                dosage = treatment.dosage,
                frequency = treatment.frequency
            )
        }

        return PatientCurrentTreatmentsResult(treatments = treatmentInfoList)
    }
    
    /**
     * Handles DiagnosisDetails query to retrieve detailed information about a specific diagnosis.
     * @param query The DiagnosisDetails query containing the diagnosis ID
     * @return DiagnosisDetailsResult with diagnosis information
     */
    @QueryHandler
    fun handle(query: DiagnosisDetails): DiagnosisDetailsResult? {
        logger.info("Processing DiagnosisDetails query for diagnosis ID: ${query.diagnosisId}")

        val diagnosis = diagnosisRepository.findById(query.diagnosisId).orElse(null)
            ?: return null

        return DiagnosisDetailsResult(
            diagnosisId = diagnosis.diagnosisId,
            condition = diagnosis.condition,
            severity = diagnosis.severity,
            diagnosisDate = diagnosis.diagnosisDate,
            doctorName = diagnosis.doctorId, // Using doctorId as placeholder for doctor name
            notes = diagnosis.notes
        )
    }

    /**
     * Event handler for TreatmentPrescribed event.
     * Creates a new treatment record when a treatment is prescribed to a patient.
     */
    @EventHandler
    @Transactional
    fun on(event: TreatmentPrescribed) {
        logger.info("Processing TreatmentPrescribed event for treatment ID: ${event.treatmentId}")

        val treatmentEntity = TreatmentEntity(
            treatmentId = event.treatmentId,
            patientId = event.patientId,
            medicationName = event.medicationName,
            dosage = event.dosage,
            frequency = event.frequency,
            duration = event.duration,
            doctorId = event.doctorId,
            status = "ACTIVE"
        )

        treatmentRepository.save(treatmentEntity)
        logger.info("Treatment prescribed and saved for patient: ${event.patientId}")
    }

    /**
     * Event handler for TreatmentDiscontinued event.
     * Updates the treatment status to discontinued when a treatment is stopped.
     */
    @EventHandler
    @Transactional
    fun on(event: TreatmentDiscontinued) {
        logger.info("Processing TreatmentDiscontinued event for treatment ID: ${event.treatmentId}")

        treatmentRepository.findById(event.treatmentId).ifPresent { treatment ->
            val updatedTreatment = treatment.copy(status = "DISCONTINUED")
            treatmentRepository.save(updatedTreatment)
            logger.info("Treatment discontinued for patient: ${event.patientId}")
        }
    }
    
    /**
     * Event handler for PatientDiagnosisRemoved event.
     * Removes a diagnosis record when it is removed from a patient's medical history.
     */
    @EventHandler
    @Transactional
    fun on(event: PatientDiagnosisRemoved) {
        logger.info("Processing PatientDiagnosisRemoved event for diagnosis ID: ${event.diagnosisId}")

        diagnosisRepository.deleteById(event.diagnosisId)
        logger.info("Diagnosis removed for patient: ${event.patientId}")
    }

    /**
     * Event handler for PatientDiagnosisRecorded event.
     * Creates a new diagnosis record when a diagnosis is recorded for a patient.
     */
    @EventHandler
    @Transactional
    fun on(event: PatientDiagnosisRecorded) {
        logger.info("Processing PatientDiagnosisRecorded event for diagnosis ID: ${event.diagnosisId}")
        
        val diagnosisEntity = DiagnosisEntity(
            diagnosisId = event.diagnosisId,
            patientId = event.patientId,
            condition = event.condition,
            severity = event.severity,
            diagnosisDate = event.diagnosisDate,
            doctorId = event.doctorId,
            notes = event.notes
        )

        diagnosisRepository.save(diagnosisEntity)
        logger.info("Diagnosis recorded for patient: ${event.patientId}")
    }
}

