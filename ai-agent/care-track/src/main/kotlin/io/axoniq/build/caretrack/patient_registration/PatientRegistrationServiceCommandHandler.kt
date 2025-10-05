package io.axoniq.build.caretrack.patient_registration

import io.axoniq.build.caretrack.patient_registration.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Patient Registration Service component.
 * This handler manages patient account registration and handles the RegisterPatient command.
 * It validates patient registration requests and publishes PatientRegistered events.
 */
class PatientRegistrationServiceCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PatientRegistrationServiceCommandHandler::class.java)
    }

    /**
     * Handles the RegisterPatient command for the Patient Registration Service.
     * Validates the patient registration data and creates a new patient account.
     * 
     * @param command The RegisterPatient command containing patient details
     * @param state The current state of the patient registration entity
     * @param eventAppender The event appender to publish events
     * @return PatientRegistrationResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: RegisterPatient,
        @InjectEntity state: PatientRegistrationServiceState,
        eventAppender: EventAppender
    ): PatientRegistrationResult {
        logger.info("Handling RegisterPatient command for email: ${command.email}")
        
        // Check if patient is already registered
        if (state.getStatus() == "REGISTERED") {
            logger.warn("Patient already registered with email: ${command.email}")
            return PatientRegistrationResult(
                patientId = "",
                registrationSuccessful = false
            )
        }
        
        // Generate unique patient ID
        val patientId = UUID.randomUUID().toString()

        // Create and append the PatientRegistered event
        val event = PatientRegistered(
            firstName = command.firstName,
            email = command.email,
            patientId = patientId,
            dateOfBirth = command.dateOfBirth,
            lastName = command.lastName
        )

        eventAppender.append(event)
        logger.info("Patient registered successfully with ID: $patientId")

        return PatientRegistrationResult(
            patientId = patientId,
            registrationSuccessful = true
        )
    }
}

