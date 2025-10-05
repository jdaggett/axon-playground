package io.axoniq.build.dance_test.instructor_management

import io.axoniq.build.dance_test.instructor_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * InstructorManagementController - REST controller for Instructor Management operations.
 * 
 * Provides HTTP endpoints for creating instructor profiles and managing Calendly integrations.
 */
@RestController
@RequestMapping("/api/instructors")
class InstructorManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstructorManagementController::class.java)
    }

    /**
     * Creates a new instructor profile.
     * 
     * @param request The CreateInstructorProfile command
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/profile")
    fun createInstructorProfile(@RequestBody request: CreateInstructorProfile): ResponseEntity<String> {
        logger.info("Received CreateInstructorProfile request for instructor: ${request.instructorId}")

        return try {
            commandGateway.sendAndWait(request)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Instructor profile creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateInstructorProfile command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create instructor profile")
        }
    }

    /**
     * Connects Calendly integration for an instructor.
     * 
     * @param request The ConnectCalendlyIntegration command
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/calendly/connect")
    fun connectCalendlyIntegration(@RequestBody request: ConnectCalendlyIntegration): ResponseEntity<String> {
        logger.info("Received ConnectCalendlyIntegration request for instructor: ${request.instructorId}")
        
        return try {
            commandGateway.sendAndWait(request)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Calendly integration connection accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ConnectCalendlyIntegration command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to connect Calendly integration")
        }
    }

    /**
     * Updates Calendly availability settings for an instructor.
     * 
     * @param request The UpdateCalendlySettings command
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/calendly/settings")
    fun updateCalendlySettings(@RequestBody request: UpdateCalendlySettings): ResponseEntity<String> {
        logger.info("Received UpdateCalendlySettings request for instructor: ${request.instructorId}")

        return try {
            commandGateway.sendAndWait(request)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Calendly settings update accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch UpdateCalendlySettings command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update Calendly settings")
        }
    }
}

