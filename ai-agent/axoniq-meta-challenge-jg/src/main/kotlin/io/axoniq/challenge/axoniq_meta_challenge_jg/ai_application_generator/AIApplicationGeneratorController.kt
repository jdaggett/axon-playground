package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator

import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the AI Application Generator component.
 * Provides endpoints for AI application generation, retries, work resumption, and completion reporting.
 */
@RestController
@RequestMapping("/api/ai-application-generator")
class AIApplicationGeneratorController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AIApplicationGeneratorController::class.java)
    }

    /**
     * Endpoint to generate AI application for the AI Application Generator component.
     */
    @PostMapping("/generate")
    fun generateAIApplication(@RequestBody request: GenerateAIApplicationRequest): ResponseEntity<String> {
        val command = GenerateAIApplication(
            participantId = request.participantId,
            applicationParameters = request.applicationParameters
        )
        logger.info("Dispatching GenerateAIApplication command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("AI application generation started")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch GenerateAIApplication command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to start AI application generation")
        }
    }

    /**
     * Endpoint to retry AI generation for the AI Application Generator component.
     */
    @PostMapping("/retry")
    fun retryAIGeneration(@RequestBody request: RetryAIGenerationRequest): ResponseEntity<String> {
        val command = RetryAIGeneration(
            participantId = request.participantId,
            originalParameters = request.originalParameters
        )
        logger.info("Dispatching RetryAIGeneration command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("AI generation retry started")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RetryAIGeneration command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to retry AI generation")
        }
    }

    /**
     * Endpoint to resume application work for the AI Application Generator component.
     */
    @PostMapping("/resume")
    fun resumeApplicationWork(@RequestBody request: ResumeApplicationWorkRequest): ResponseEntity<String> {
        val command = ResumeApplicationWork(
            participantId = request.participantId,
            sessionToken = request.sessionToken
        )
        logger.info("Dispatching ResumeApplicationWork command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Application work resumed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ResumeApplicationWork command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to resume application work")
        }
    }

    /**
     * Endpoint to report generation completion for the AI Application Generator component.
     */
    @PostMapping("/report-completion")
    fun reportGenerationCompletion(@RequestBody request: ReportGenerationCompletionRequest): ResponseEntity<String> {
        val command = ReportGenerationCompletion(
            applicationId = request.applicationId,
            participantId = request.participantId,
            isSuccessful = request.isSuccessful
        )
        logger.info("Dispatching ReportGenerationCompletion command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Generation completion reported")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ReportGenerationCompletion command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report generation completion")
        }
    }
}

/**
 * Request data classes for the AI Application Generator controller endpoints.
 */
data class GenerateAIApplicationRequest(
    val participantId: String,
    val applicationParameters: String
)

data class RetryAIGenerationRequest(
    val participantId: String,
    val originalParameters: String
)

data class ResumeApplicationWorkRequest(
    val participantId: String,
    val sessionToken: String
)

data class ReportGenerationCompletionRequest(
    val applicationId: String,
    val participantId: String,
    val isSuccessful: Boolean
)

