package io.axoniq.build.caretrack.account_deletion_service

import io.axoniq.build.caretrack.account_deletion_service.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for Account Deletion Service
 * Handles commands for deleting patient and doctor accounts
 */
class AccountDeletionServiceCommandHandler {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AccountDeletionServiceCommandHandler::class.java)
    }

    /**
     * Handles DeletePatientAccount command
     * Validates the request and emits PatientAccountDeleted event
     */
    @CommandHandler
    fun handle(
        command: DeletePatientAccount,
        @InjectEntity state: AccountDeletionState,
        eventAppender: EventAppender
    ): PatientAccountDeletionResult {
        logger.info("Processing DeletePatientAccount command for patientId: ${command.patientId}")

        // Validate confirmation code (simplified validation)
        if (command.confirmationCode.isBlank()) {
            throw IllegalArgumentException("Confirmation code cannot be blank")
        }

        // Append the deletion event
        val event = PatientAccountDeleted(patientId = command.patientId)
        eventAppender.append(event)

        logger.info("Patient account deletion event appended for patientId: ${command.patientId}")
        return PatientAccountDeletionResult(accountDeleted = true)
    }

    /**
     * Handles DeleteDoctorAccount command
     * Validates the request and emits DoctorAccountDeleted event
     */
    @CommandHandler
    fun handle(
        command: DeleteDoctorAccount,
        @InjectEntity state: AccountDeletionState,
        eventAppender: EventAppender
    ): DoctorAccountDeletionResult {
        logger.info("Processing DeleteDoctorAccount command for doctorId: ${command.doctorId}")

        // Validate confirmation code (simplified validation)
        if (command.confirmationCode.isBlank()) {
            throw IllegalArgumentException("Confirmation code cannot be blank")
        }

        // Append the deletion event
        val event = DoctorAccountDeleted(doctorId = command.doctorId)
        eventAppender.append(event)

        logger.info("Doctor account deletion event appended for doctorId: ${command.doctorId}")
        return DoctorAccountDeletionResult(accountDeleted = true)
    }
}

