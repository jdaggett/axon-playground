package io.axoniq.build.caretrack.medical_record_management

import io.axoniq.build.caretrack.medical_record_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

/**
 * Event-sourced entity representing the medical record management state for a patient.
 * Maintains the current state of diagnoses and treatments based on past events.
 */
@EventSourcedEntity
class MedicalRecordManagementState {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MedicalRecordManagementState::class.java)
        
        /**
         * Builds the EventCriteria to load events for reconstructing the medical record state.
         * Filters events by patient identifier and relevant event types.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(patientId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Patient", patientId))
                .andBeingOneOfTypes(
                    PatientDiagnosisRecorded::class.java.name,
                    PatientDiagnosisRemoved::class.java.name,
                    TreatmentPrescribed::class.java.name,
                    TreatmentDiscontinued::class.java.name
                )
        }
    }

    private var patientId: String? = null
    private val diagnoses: MutableList<Diagnosis> = mutableListOf()
    private val treatments: MutableList<Treatment> = mutableListOf()
    
    fun getPatientId(): String? = patientId
    fun getDiagnoses(): List<Diagnosis> = diagnoses.toList()
    fun getTreatments(): List<Treatment> = treatments.toList()
    
    @EntityCreator
    constructor()
    
    /**
     * Handles PatientDiagnosisRecorded event to add a new diagnosis to the patient's medical record.
     */
    @EventSourcingHandler
    fun evolve(event: PatientDiagnosisRecorded) {
        logger.debug("Evolving state with PatientDiagnosisRecorded for patient: ${event.patientId}")

        if (patientId == null) {
            patientId = event.patientId
        }

        val diagnosis = Diagnosis(
            diagnosisId = event.diagnosisId,
            condition = event.condition,
            severity = event.severity
        )

        diagnoses.add(diagnosis)
    }

    /**
     * Handles PatientDiagnosisRemoved event to remove a diagnosis from the patient's medical record.
     */
    @EventSourcingHandler
    fun evolve(event: PatientDiagnosisRemoved) {
        logger.debug("Evolving state with PatientDiagnosisRemoved for patient: ${event.patientId}")

        if (patientId == null) {
            patientId = event.patientId
        }

        diagnoses.removeAll { it.diagnosisId == event.diagnosisId }
    }

    /**
     * Handles TreatmentPrescribed event to add a new treatment to the patient's medical record.
     */
    @EventSourcingHandler
    fun evolve(event: TreatmentPrescribed) {
        logger.debug("Evolving state with TreatmentPrescribed for patient: ${event.patientId}")

        if (patientId == null) {
            patientId = event.patientId
        }

        val treatment = Treatment(
            treatmentId = event.treatmentId,
            medicationName = event.medicationName,
            dosage = event.dosage,
            status = "ACTIVE"
        )

        treatments.add(treatment)
    }

    /**
     * Handles TreatmentDiscontinued event to update treatment status to discontinued.
     */
    @EventSourcingHandler
    fun evolve(event: TreatmentDiscontinued) {
        logger.debug("Evolving state with TreatmentDiscontinued for patient: ${event.patientId}")

        if (patientId == null) {
            patientId = event.patientId
        }

        val treatment = treatments.find { it.treatmentId == event.treatmentId }
        if (treatment != null) {
            val updatedTreatment = treatment.copy(status = "DISCONTINUED")
            val index = treatments.indexOf(treatment)
            treatments[index] = updatedTreatment
        }
    }

    /**
     * Inner class representing a patient diagnosis.
     */
    data class Diagnosis(
        val diagnosisId: String,
        val condition: String,
        val severity: String
    )
    
    /**
     * Inner class representing a patient treatment.
     */
    data class Treatment(
        val treatmentId: String,
        val medicationName: String,
        val dosage: String,
        val status: String
    )
}

