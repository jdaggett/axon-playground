package io.axoniq.build.caretrack.patient_registration

import io.axoniq.build.caretrack.patient_registration.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Patient Registration Service component.
 * Provides HTTP endpoints for patient registration functionality.
 */
@RestController
@RequestMapping("/api/patient-registration")
class PatientRegistrationServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PatientRegistrationServiceController::class.java)
    }

    /**
     * Endpoint for registering a new patient.
     * Accepts patient registration details and dispatches the RegisterPatient command.
     * 
     * @param command The RegisterPatient command with patient details
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/register")
    fun registerPatient(@RequestBody command: RegisterPatient): ResponseEntity<String> {
        logger.info("Received patient registration request for email: ${command.email}")

        return try {
            commandGateway.sendAndWait(command)
            logger.info("Patient registration command dispatched successfully for email: ${command.email}")
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Patient registration accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RegisterPatient command for email: ${command.email}", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register patient")
        }
    }
}

