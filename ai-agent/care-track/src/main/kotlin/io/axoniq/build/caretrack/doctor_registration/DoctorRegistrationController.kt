package io.axoniq.build.caretrack.doctor_registration

import io.axoniq.build.caretrack.doctor_registration.api.RegisterDoctor
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Doctor Registration Service component.
 * Exposes HTTP endpoints for doctor registration and management operations.
 */
@RestController
@RequestMapping("/api/doctor-registration")
class DoctorRegistrationController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DoctorRegistrationController::class.java)
    }

    /**
     * Registers a new doctor in the system.
     * 
     * @param request The RegisterDoctor request containing doctor registration details
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/register")
    fun registerDoctor(@RequestBody request: RegisterDoctor): ResponseEntity<String> {
        logger.info("Received doctor registration request for email: ${request.email}")

        return try {
            commandGateway.sendAndWait(request)
            logger.info("Successfully dispatched RegisterDoctor command for email: ${request.email}")
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Doctor registration accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RegisterDoctor command for email: ${request.email}", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to register doctor")
        }
    }
}

