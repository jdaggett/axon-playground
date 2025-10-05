package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_generation_service

import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_generation_service.api.AIGenerationStarted
import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_generation_service.api.ReportGenerationCompletion
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * AI Generation Service Integration - External System Component
 * 
 * This component handles AI application generation requests and completion reporting.
 * It processes AIGenerationStarted events by triggering external AI generation processes
 * and reports completion status back to the system via ReportGenerationCompletion commands.
 */
@Service
class AIGenerationServiceIntegration(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(AIGenerationServiceIntegration::class.java)

    /**
     * Processes AI Generation Request
     * 
     * Handles the AIGenerationStarted event by initiating the AI application generation process
     * for the specified participant. This is a stub implementation that logs the action
     * and sends a completion report back to the system.
     * 
     * @param event The AIGenerationStarted event containing participant ID and application parameters
     * @param processingContext The processing context for command gateway operations
     */
    @EventHandler
    fun handle(event: AIGenerationStarted, processingContext: ProcessingContext) {
        logger.info("Processing AI generation request for participant: ${event.participantId}")
        logger.info("Application parameters: ${event.applicationParameters}")

        // Log the external system action to be performed
        logger.info("Initiating AI application generation process for participant ${event.participantId}")

        // Simulate AI generation completion and report back to the system
        val completionCommand = ReportGenerationCompletion(
            applicationId = "generated-app-${event.participantId}",
            participantId = event.participantId,
            isSuccessful = true
        )
        
        logger.info("Sending generation completion report for participant: ${event.participantId}")
        commandGateway.send(completionCommand, processingContext)
    }
}