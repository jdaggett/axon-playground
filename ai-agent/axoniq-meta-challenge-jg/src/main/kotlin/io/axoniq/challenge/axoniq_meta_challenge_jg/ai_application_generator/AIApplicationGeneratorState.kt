package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator

import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Event-sourced entity that maintains the state of AI application generation for the AI Application Generator component.
 * This entity tracks generation status, retry attempts, and participant information throughout the application generation lifecycle.
 */
@EventSourcedEntity
class AIApplicationGeneratorState {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AIApplicationGeneratorState::class.java)

        @EventCriteriaBuilder
        fun resolveCriteria(participantId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Participant", participantId))
                .andBeingOneOfTypes(
                    AIGenerationStarted::class.java.name,
                    AIGenerationRetried::class.java.name,
                    ApplicationWorkResumed::class.java.name,
                    ApplicationGeneratedSuccessfully::class.java.name,
                    PartialApplicationCreated::class.java.name
                )
        }
    }

    private var participantId: String? = null
    private var applicationId: String? = null
    private var generationStatus: String = "PENDING"
    private var isPartiallyComplete: Boolean = false
    private var retryCount: Int = 0

    fun getParticipantId(): String? = participantId
    fun getApplicationId(): String? = applicationId
    fun getGenerationStatus(): String = generationStatus
    fun getIsPartiallyComplete(): Boolean = isPartiallyComplete
    fun getRetryCount(): Int = retryCount

    @EntityCreator
    constructor()

    /**
     * Evolves the state when AI generation is started for the AI Application Generator component.
     */
    @EventSourcingHandler
    fun evolve(event: AIGenerationStarted) {
        participantId = event.participantId
        generationStatus = "IN_PROGRESS"
        logger.debug("AI generation started for participant: ${event.participantId}")
    }

    /**
     * Evolves the state when AI generation is retried for the AI Application Generator component.
     */
    @EventSourcingHandler
    fun evolve(event: AIGenerationRetried) {
        retryCount = event.retryAttempt
        generationStatus = "RETRYING"
        logger.debug("AI generation retried, attempt ${event.retryAttempt} for participant: ${event.participantId}")
    }

    /**
     * Evolves the state when application work is resumed for the AI Application Generator component.
     */
    @EventSourcingHandler
    fun evolve(event: ApplicationWorkResumed) {
        applicationId = event.applicationId
        generationStatus = "RESUMED"
        logger.debug("Application work resumed for participant: ${event.participantId}, application: ${event.applicationId}")
    }

    /**
     * Evolves the state when application is generated successfully for the AI Application Generator component.
     */
    @EventSourcingHandler
    fun evolve(event: ApplicationGeneratedSuccessfully) {
        applicationId = event.applicationId
        generationStatus = "COMPLETED"
        isPartiallyComplete = false
        logger.debug("Application generated successfully for participant: ${event.participantId}, application: ${event.applicationId}")
    }

    /**
     * Evolves the state when partial application is created for the AI Application Generator component.
     */
    @EventSourcingHandler
    fun evolve(event: PartialApplicationCreated) {
        applicationId = event.applicationId
        generationStatus = "PARTIAL"
        isPartiallyComplete = true
        logger.debug("Partial application created for participant: ${event.participantId}, application: ${event.applicationId}")
    }
}

