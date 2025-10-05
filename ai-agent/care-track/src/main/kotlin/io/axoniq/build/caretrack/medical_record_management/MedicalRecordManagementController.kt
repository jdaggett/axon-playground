package io.axoniq.build.caretrack.medical_record_management

import io.axoniq.build.caretrack.medical_record_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Medical Record Management Service.
 * Exposes HTTP endpoints for managing medical diagnoses and treatments.
 */
@RestController
@RequestMapping("/api/medical-records")
class MedicalRecordManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(MedicalRecordManagementController::class.java)
    }

    /**
     * Endpoint to enter a patient diagnosis.
     */
    @PostMapping("/diagnoses")
    fun enterPatientDiagnosis(@RequestBody command: EnterPatientDiagnosis): ResponseEntity<String> {
        logger.info("Dispatching EnterPatientDiagnosis command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Diagnosis entry accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch EnterPatientDiagnosis command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to enter patient diagnosis")
        }
    }

    /**
     * Endpoint to remove a patient diagnosis.
     */
    @DeleteMapping("/patients/{patientId}/diagnoses/{diagnosisId}")
    fun removePatientDiagnosis(
        @PathVariable patientId: String,
        @PathVariable diagnosisId: String,
        @RequestParam doctorId: String
    ): ResponseEntity<String> {
        val command = RemovePatientDiagnosis(
            doctorId = doctorId,
            patientId = patientId,
            diagnosisId = diagnosisId
        )
        logger.info("Dispatching RemovePatientDiagnosis command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Diagnosis removal accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RemovePatientDiagnosis command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove patient diagnosis")
        }
    }

    /**
     * Endpoint to prescribe a treatment.
     */
    @PostMapping("/treatments")
    fun prescribeTreatment(@RequestBody command: PrescribeTreatment): ResponseEntity<String> {
        logger.info("Dispatching PrescribeTreatment command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Treatment prescription accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch PrescribeTreatment command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to prescribe treatment")
        }
    }

    /**
     * Endpoint to discontinue a treatment.
     */
    @PutMapping("/patients/{patientId}/treatments/{treatmentId}/discontinue")
    fun discontinueTreatment(
        @PathVariable patientId: String,
        @PathVariable treatmentId: String,
        @RequestParam doctorId: String,
        @RequestParam(required = false) reason: String?
    ): ResponseEntity<String> {
        val command = DiscontinueTreatment(
            doctorId = doctorId,
            reason = reason,
            patientId = patientId,
            treatmentId = treatmentId
        )
        logger.info("Dispatching DiscontinueTreatment command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Treatment discontinuation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DiscontinueTreatment command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to discontinue treatment")
        }
    }
}

