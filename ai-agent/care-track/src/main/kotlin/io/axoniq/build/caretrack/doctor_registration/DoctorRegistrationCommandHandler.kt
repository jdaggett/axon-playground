package io.axoniq.build.caretrack.doctor_registration

import io.axoniq.build.caretrack.doctor_registration.api.DoctorRegistered
import io.axoniq.build.caretrack.doctor_registration.api.DoctorRegistrationResult
import io.axoniq.build.caretrack.doctor_registration.api.RegisterDoctor
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Doctor Registration Service component.
 * Handles doctor account registration and management operations.
 */
class DoctorRegistrationCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DoctorRegistrationCommandHandler::class.java)
    }

    /**
     * Handles the RegisterDoctor command to register a new doctor in the system.
     * Validates that the doctor is not already registered and creates a new doctor registration.
     * 
     * @param command The RegisterDoctor command containing doctor registration details
     * @param state The current state of the doctor registration entity
     * @param eventAppender Used to append events to the event stream
     * @return DoctorRegistrationResult indicating success or failure with doctor ID
     */
    @CommandHandler
    fun handle(
        command: RegisterDoctor,
        @InjectEntity state: DoctorRegistrationState,
        eventAppender: EventAppender
    ): DoctorRegistrationResult {
        logger.info("Handling RegisterDoctor command for email: ${command.email}")

        // Validate that the doctor is not already registered
        if (state.getStatus() == "REGISTERED") {
            logger.warn("Doctor with email ${command.email} is already registered")
            return DoctorRegistrationResult(
                registrationSuccessful = false,
                doctorId = ""
            )
        }

        // Generate a unique doctor ID
        val doctorId = UUID.randomUUID().toString()

        // Create and append the DoctorRegistered event
        val event = DoctorRegistered(
            firstName = command.firstName,
            email = command.email,
            medicalLicenseNumber = command.medicalLicenseNumber,
            specialization = command.specialization,
            lastName = command.lastName,
            doctorId = doctorId
        )

        eventAppender.append(event)
        logger.info("Successfully registered doctor with ID: $doctorId")

        return DoctorRegistrationResult(
            registrationSuccessful = true,
            doctorId = doctorId
        )
    }
}

