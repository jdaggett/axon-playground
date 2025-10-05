package io.axoniq.build.dance_test.session_management

import io.axoniq.build.dance_test.session_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Session Management component.
 * Provides endpoints for session modification, completion and no-show processing.
 */
@RestController
@RequestMapping("/api/sessions")
class SessionManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SessionManagementController::class.java)
    }

    /**
     * Endpoint to complete a training session.
     * Accepts session completion details and dispatches the CompleteTrainingSession command.
     */
    @PostMapping("/{sessionId}/complete")
    fun completeTrainingSession(
        @PathVariable sessionId: String,
        @RequestBody request: CompleteTrainingSessionRequest
    ): ResponseEntity<String> {
        val command = CompleteTrainingSession(
            sessionId = sessionId,
            actualDuration = request.actualDuration,
            notes = request.notes
        )
        logger.info("Dispatching CompleteTrainingSession command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Session completion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CompleteTrainingSession command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to complete training session")
        }
    }

    /**
     * Endpoint to modify session details.
     * Accepts session modification details and dispatches the ModifySessionDetails command.
     */
    @PutMapping("/{sessionId}/details")
    fun modifySessionDetails(
        @PathVariable sessionId: String,
        @RequestBody request: ModifySessionDetailsRequest
    ): ResponseEntity<String> {
        val command = ModifySessionDetails(
            sessionId = sessionId,
            newDuration = request.newDuration,
            newSessionDate = request.newSessionDate
        )
        logger.info("Dispatching ModifySessionDetails command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Session modification accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ModifySessionDetails command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to modify session details")
        }
    }

    /**
     * Endpoint to mark a session as no-show.
     * Accepts no-show details and dispatches the MarkSessionAsNoShow command.
     */
    @PostMapping("/{sessionId}/no-show")
    fun markSessionAsNoShow(
        @PathVariable sessionId: String,
        @RequestBody request: MarkSessionAsNoShowRequest
    ): ResponseEntity<String> {
        val command = MarkSessionAsNoShow(
            sessionId = sessionId,
            reason = request.reason,
            chargeStudent = request.chargeStudent
        )
        logger.info("Dispatching MarkSessionAsNoShow command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("No-show marking accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch MarkSessionAsNoShow command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to mark session as no-show")
        }
    }

    /**
     * Endpoint to complete a session with reduced time.
     * Accepts session completion details and dispatches the CompleteSessionWithReducedTime command.
     */
    @PostMapping("/{sessionId}/complete-reduced")
    fun completeSessionWithReducedTime(
        @PathVariable sessionId: String,
        @RequestBody request: CompleteSessionWithReducedTimeRequest
    ): ResponseEntity<String> {
        val command = CompleteSessionWithReducedTime(
            sessionId = sessionId,
            actualDuration = request.actualDuration,
            notes = request.notes
        )
        logger.info("Dispatching CompleteSessionWithReducedTime command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Reduced time session completion accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CompleteSessionWithReducedTime command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to complete session with reduced time")
        }
    }
}

/**
 * Request model for completing a training session.
 */
data class CompleteTrainingSessionRequest(
    val actualDuration: Int,
    val notes: String?
)

/**
 * Request model for modifying session details.
 */
data class ModifySessionDetailsRequest(
    val newDuration: Int?,
    val newSessionDate: java.time.LocalDateTime?
)

/**
 * Request model for marking a session as no-show.
 */
data class MarkSessionAsNoShowRequest(
    val reason: String,
    val chargeStudent: Boolean
)

/**
 * Request model for completing a session with reduced time.
 */
data class CompleteSessionWithReducedTimeRequest(
    val actualDuration: Int,
    val notes: String?
)

