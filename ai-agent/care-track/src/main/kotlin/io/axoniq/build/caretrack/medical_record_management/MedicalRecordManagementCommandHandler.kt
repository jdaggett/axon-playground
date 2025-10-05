package io.axoniq.build.caretrack.medical_record_management

import io.axoniq.build.caretrack.medical_record_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Medical Record Management Service.
 * Handles commands for managing medical diagnosis and treatment for patients.
 */
class MedicalRecordManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MedicalRecordManagementCommandHandler::class.java)
    }

    /**
     * Handles the EnterPatientDiagnosis command.
     * Records a new diagnosis for a patient in the medical record management system.
     */
    @CommandHandler
    fun handle(
        command: EnterPatientDiagnosis,
        @InjectEntity state: MedicalRecordManagementState,
        eventAppender: EventAppender
    ): DiagnosisEntryResult {
        logger.info("Processing EnterPatientDiagnosis command for patient: ${command.patientId}")

        val diagnosisId = UUID.randomUUID().toString()

        val event = PatientDiagnosisRecorded(
            doctorId = command.doctorId,
            patientId = command.patientId,
            severity = command.severity,
            notes = command.notes,
            diagnosisId = diagnosisId,
            condition = command.condition,
            diagnosisDate = command.diagnosisDate
        )

        eventAppender.append(event)

        return DiagnosisEntryResult(
            diagnosisRecorded = true,
            diagnosisId = diagnosisId
        )
    }

    /**
     * Handles the RemovePatientDiagnosis command.
     * Removes an existing diagnosis from a patient's medical record.
     */
    @CommandHandler
    fun handle(
        command: RemovePatientDiagnosis,
        @InjectEntity state: MedicalRecordManagementState,
        eventAppender: EventAppender
    ): DiagnosisRemovalResult {
        logger.info("Processing RemovePatientDiagnosis command for patient: ${command.patientId}, diagnosis: ${command.diagnosisId}")

        // Check if diagnosis exists
        val diagnosisExists = state.getDiagnoses().any { it.diagnosisId == command.diagnosisId }
        
        if (!diagnosisExists) {
            logger.warn("Diagnosis ${command.diagnosisId} not found for patient ${command.patientId}")
            return DiagnosisRemovalResult(diagnosisRemoved = false)
        }

        val event = PatientDiagnosisRemoved(
            doctorId = command.doctorId,
            patientId = command.patientId,
            diagnosisId = command.diagnosisId
        )

        eventAppender.append(event)

        return DiagnosisRemovalResult(diagnosisRemoved = true)
    }

    /**
     * Handles the PrescribeTreatment command.
     * Prescribes a new treatment for a patient.
     */
    @CommandHandler
    fun handle(
        command: PrescribeTreatment,
        @InjectEntity state: MedicalRecordManagementState,
        eventAppender: EventAppender
    ): TreatmentPrescriptionResult {
        logger.info("Processing PrescribeTreatment command for patient: ${command.patientId}")

        val treatmentId = UUID.randomUUID().toString()

        val event = TreatmentPrescribed(
            doctorId = command.doctorId,
            frequency = command.frequency,
            dosage = command.dosage,
            patientId = command.patientId,
            medicationName = command.medicationName,
            duration = command.duration,
            treatmentId = treatmentId
        )

        eventAppender.append(event)

        return TreatmentPrescriptionResult(
            treatmentPrescribed = true,
            treatmentId = treatmentId
        )
    }

    /**
     * Handles the DiscontinueTreatment command.
     * Discontinues an existing treatment for a patient.
     */
    @CommandHandler
    fun handle(
        command: DiscontinueTreatment,
        @InjectEntity state: MedicalRecordManagementState,
        eventAppender: EventAppender
    ): TreatmentDiscontinuationResult {
        logger.info("Processing DiscontinueTreatment command for patient: ${command.patientId}, treatment: ${command.treatmentId}")

        // Check if treatment exists and is active
        val treatment = state.getTreatments().find { it.treatmentId == command.treatmentId }

        if (treatment == null) {
            logger.warn("Treatment ${command.treatmentId} not found for patient ${command.patientId}")
            return TreatmentDiscontinuationResult(treatmentDiscontinued = false)
        }

        if (treatment.status == "DISCONTINUED") {
            logger.warn("Treatment ${command.treatmentId} is already discontinued")
            return TreatmentDiscontinuationResult(treatmentDiscontinued = false)
        }

        val event = TreatmentDiscontinued(
            doctorId = command.doctorId,
            reason = command.reason,
            patientId = command.patientId,
            treatmentId = command.treatmentId
        )

        eventAppender.append(event)

        return TreatmentDiscontinuationResult(treatmentDiscontinued = true)
    }
}

