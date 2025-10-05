package io.axoniq.build.pet_clinic.pet_management

import io.axoniq.build.pet_clinic.pet_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

/**
 * REST controller for Pet Management component.
 * Exposes endpoints for pet registration operations.
 */
@RestController
@RequestMapping("/api/pet-management")
class PetManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PetManagementController::class.java)
    }

    /**
     * Endpoint for registering a new pet.
     * Accepts pet registration data and dispatches RegisterPet command.
     */
    @PostMapping("/register")
    fun registerPet(@RequestBody request: RegisterPetRequest): ResponseEntity<String> {
        val command = RegisterPet(
            petId = request.petId,
            email = request.email,
            name = request.name,
            birthday = request.birthday,
            type = request.type
        )

        logger.info("Dispatching RegisterPet command for pet: ${request.name}")
        
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Pet registration accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RegisterPet command for pet: ${request.name}", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register pet")
        }
    }
}

/**
 * Request model for pet registration endpoint.
 */
data class RegisterPetRequest(
    val petId: String,
    val email: String,
    val name: String,
    val birthday: Date,
    val type: String
)

