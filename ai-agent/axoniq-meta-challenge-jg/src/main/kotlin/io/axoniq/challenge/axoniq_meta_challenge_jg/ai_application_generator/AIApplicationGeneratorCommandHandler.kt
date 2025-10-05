package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator

import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.exception.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the AI Application Generator component.
 * Handles commands related to AI application generation, retries, work resumption, and completion reporting.
 */
class AIApplicationGeneratorCommandHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AIApplicationGeneratorCommandHandler::class.java)
        private const val MAX_RETRY_ATTEMPTS = 3
    }

    /**
     * Handles the GenerateAIApplication command for the AI Application Generator component.
     * Starts the AI application generation process and validates the current state.
     */
    @CommandHandler
    fun handle(
        command: GenerateAIApplication,
        @InjectEntity state: AIApplicationGeneratorState,
        eventAppender: EventAppender
    ): ApplicationGenerationResult {
        logger.info("Processing GenerateAIApplication command for participant: ${command.participantId}")

        // Validate that generation is not already in progress
        if (state.getGenerationStatus() == "IN_PROGRESS") {
            throw AIGenerationFailed("AI generation is already in progress for participant: ${command.participantId}")
        }

        // Simulate AI generation failure scenarios based on parameters
        when {
            command.applicationParameters.contains("FORCE_FAIL") -> {
                throw AIGenerationFailed("AI generation failed for participant: ${command.participantId}")
            }
            command.applicationParameters.contains("FORCE_PARTIAL") -> {
                throw AIGenerationPartiallyFailed("AI generation partially failed for participant: ${command.participantId}")
            }
            else -> {
                // Start generation process
                val event = AIGenerationStarted(
                    participantId = command.participantId,
                    applicationParameters = command.applicationParameters
                )
                eventAppender.append(event)

                return ApplicationGenerationResult(
                    applicationId = UUID.randomUUID().toString(),
                    isSuccessful = true
                )
            }
        }
    }

    /**
     * Handles the RetryAIGeneration command for the AI Application Generator component.
     * Retries the AI application generation process after a previous failure.
     */
    @CommandHandler
    fun handle(
        command: RetryAIGeneration,
        @InjectEntity state: AIApplicationGeneratorState,
        eventAppender: EventAppender
    ): ApplicationGenerationResult {
        logger.info("Processing RetryAIGeneration command for participant: ${command.participantId}")

        val currentRetryCount = state.getRetryCount()
        val newRetryCount = currentRetryCount + 1

        // Validate retry limit
        if (newRetryCount > MAX_RETRY_ATTEMPTS) {
            logger.warn("Maximum retry attempts exceeded for participant: ${command.participantId}")
            return ApplicationGenerationResult(
                applicationId = null,
                isSuccessful = false
            )
        }

        // Emit retry event
        val event = AIGenerationRetried(
            retryAttempt = newRetryCount,
            participantId = command.participantId
        )
        eventAppender.append(event)

        return ApplicationGenerationResult(
            applicationId = UUID.randomUUID().toString(),
            isSuccessful = true
        )
    }

    /**
     * Handles the ResumeApplicationWork command for the AI Application Generator component.
     * Resumes previously interrupted application generation work.
     */
    @CommandHandler
    fun handle(
        command: ResumeApplicationWork,
        @InjectEntity state: AIApplicationGeneratorState,
        eventAppender: EventAppender
    ): ApplicationResumeResult {
        logger.info("Processing ResumeApplicationWork command for participant: ${command.participantId}")

        // Validate session token (simplified validation)
        if (command.sessionToken.isBlank() || command.sessionToken.length < 10) {
            throw BrowserSessionLost("Invalid or expired session token for participant: ${command.participantId}")
        }

        // Check if there's work to resume
        if (state.getGenerationStatus() == "COMPLETED") {
            logger.warn("No work to resume - generation already completed for participant: ${command.participantId}")
            return ApplicationResumeResult(isSuccessful = false)
        }

        // Generate new application ID for resumed work
        val applicationId = state.getApplicationId() ?: UUID.randomUUID().toString()

        val event = ApplicationWorkResumed(
            applicationId = applicationId,
            participantId = command.participantId
        )
        eventAppender.append(event)

        return ApplicationResumeResult(isSuccessful = true)
    }

    /**
     * Handles the ReportGenerationCompletion command for the AI Application Generator component.
     * Reports the completion status of AI application generation.
     */
    @CommandHandler
    fun handle(
        command: ReportGenerationCompletion,
        @InjectEntity state: AIApplicationGeneratorState,
        eventAppender: EventAppender
    ): GenerationCompletionResult {
        logger.info("Processing ReportGenerationCompletion command for participant: ${command.participantId}")

        val event = if (command.isSuccessful) {
            ApplicationGeneratedSuccessfully(
                applicationId = command.applicationId,
                participantId = command.participantId
            )
        } else {
            PartialApplicationCreated(
                applicationId = command.applicationId,
                participantId = command.participantId
            )
        }

        eventAppender.append(event)

        return GenerationCompletionResult(isSuccessful = true)
    }
}

