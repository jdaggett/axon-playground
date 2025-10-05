package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for Challenge Management operations.
 * Exposes endpoints for challenge initialization, completion checking, and restart attempts.
 */
@RestController
@RequestMapping("/api/challenge-management")
class ChallengeManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChallengeManagementController::class.java)
    }

    /**
     * Endpoint to begin a challenge for a participant
     */
    @PostMapping("/begin")
    fun beginChallenge(@RequestBody request: BeginChallengeRequest): ResponseEntity<String> {
        val command = BeginChallenge(participantId = request.participantId)
        logger.info("Dispatching BeginChallenge command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Challenge begin request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch BeginChallenge command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to begin challenge")
        }
    }

    /**
     * Endpoint to check challenge completion status for a participant
     */
    @PostMapping("/check-completion")
    fun checkChallengeCompletion(@RequestBody request: CheckChallengeCompletionRequest): ResponseEntity<String> {
        val command = CheckChallengeCompletion(participantId = request.participantId)
        logger.info("Dispatching CheckChallengeCompletion command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Challenge completion check accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CheckChallengeCompletion command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to check challenge completion")
        }
    }

    /**
     * Endpoint to attempt challenge restart for a participant
     */
    @PostMapping("/attempt-restart")
    fun attemptChallengeRestart(@RequestBody request: AttemptChallengeRestartRequest): ResponseEntity<String> {
        val command = AttemptChallengeRestart(participantId = request.participantId)
        logger.info("Dispatching AttemptChallengeRestart command: $command")
        
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Challenge restart attempt accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AttemptChallengeRestart command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to attempt challenge restart")
        }
    }
}

/**
 * Request data classes for REST endpoints
 */
data class BeginChallengeRequest(
    val participantId: String
)

data class CheckChallengeCompletionRequest(
    val participantId: String
)

data class AttemptChallengeRestartRequest(
    val participantId: String
)

