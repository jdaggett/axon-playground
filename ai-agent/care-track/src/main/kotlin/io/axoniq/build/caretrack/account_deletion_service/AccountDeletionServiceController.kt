package io.axoniq.build.caretrack.account_deletion_service

import io.axoniq.build.caretrack.account_deletion_service.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Account Deletion Service
 * Handles HTTP endpoints for deleting patient and doctor accounts
 */
@RestController
@RequestMapping("/api/account-deletion")
class AccountDeletionServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AccountDeletionServiceController::class.java)
    }

    /**
     * Endpoint to delete a patient account
     * Maps to DeletePatientAccount command
     */
    @DeleteMapping("/patient/{patientId}")
    fun deletePatientAccount(
        @PathVariable patientId: String,
        @RequestBody request: DeletePatientAccountRequest
    ): ResponseEntity<String> {
        val command = DeletePatientAccount(
            confirmationCode = request.confirmationCode,
            patientId = patientId
        )
        logger.info("Dispatching DeletePatientAccount command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Patient account deletion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DeletePatientAccount command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete patient account")
        }
    }

    /**
     * Endpoint to delete a doctor account
     * Maps to DeleteDoctorAccount command
     */
    @DeleteMapping("/doctor/{doctorId}")
    fun deleteDoctorAccount(
        @PathVariable doctorId: String,
        @RequestBody request: DeleteDoctorAccountRequest
    ): ResponseEntity<String> {
        val command = DeleteDoctorAccount(
            confirmationCode = request.confirmationCode,
            doctorId = doctorId
        )
        logger.info("Dispatching DeleteDoctorAccount command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Doctor account deletion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DeleteDoctorAccount command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to delete doctor account")
        }
    }

    data class DeletePatientAccountRequest(
        val confirmationCode: String
    )

    data class DeleteDoctorAccountRequest(
        val confirmationCode: String
    )
}

