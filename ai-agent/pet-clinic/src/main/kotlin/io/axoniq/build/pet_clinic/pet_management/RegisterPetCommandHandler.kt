package io.axoniq.build.pet_clinic.pet_management

import io.axoniq.build.pet_clinic.pet_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for Pet Management component.
 * Handles pet registration and related operations.
 */
class RegisterPetCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RegisterPetCommandHandler::class.java)
    }

    /**
     * Handles RegisterPet command for pet registration.
     * Validates the command and publishes PetRegistered event.
     */
    @CommandHandler
    fun handle(
        command: RegisterPet,
        @InjectEntity state: PetManagementState,
        eventAppender: EventAppender
    ) {
        logger.info("Handling RegisterPet command for pet: ${command.name}")

        // Validate that pet is not already registered
        if (state.getRegistrationStatus() == "REGISTERED") {
            throw IllegalStateException("Pet is already registered")
        }

        // Create and append the PetRegistered event
        val event = PetRegistered(
            petId = command.petId,
            email = command.email,
            name = command.name,
            birthday = command.birthday,
            type = command.type
        )

        logger.info("Publishing PetRegistered event for pet: ${command.name}")
        eventAppender.append(event)
    }
}

