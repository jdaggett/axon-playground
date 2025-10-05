package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Challenge Management State entity that tracks participant progress through the challenge.
 * This entity maintains the current status and completion criteria for each participant.
 */
@EventSourcedEntity
class ChallengeManagementState {
    private var participantId: String? = null
    private var challengeStatus: String = "NOT_STARTED"
    private var voteCast: Boolean = false
    private var applicationCreated: Boolean = false
    private var isEligibleForPrize: Boolean = false
    private var projectShared: Boolean = false

    // Getters for accessing private state in command handlers
    fun getParticipantId(): String? = participantId
    fun getChallengeStatus(): String = challengeStatus
    fun getVoteCast(): Boolean = voteCast
    fun getApplicationCreated(): Boolean = applicationCreated
    fun getIsEligibleForPrize(): Boolean = isEligibleForPrize
    fun getProjectShared(): Boolean = projectShared

    @EntityCreator
    constructor()

    /**
     * Handles ChallengeStarted event to initialize participant state
     */
    @EventSourcingHandler
    fun evolve(event: ChallengeStarted) {
        this.participantId = event.participantId
        this.challengeStatus = "STARTED"
    }

    /**
     * Handles VoteRegistered event to track voting completion
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: VoteRegistered) {
        this.voteCast = true
    }

    /**
     * Handles ApplicationGeneratedSuccessfully event to track application creation
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: ApplicationGeneratedSuccessfully) {
        this.applicationCreated = true
    }

    /**
     * Handles EligibilityDetermined event to update prize eligibility status
     */
    @EventSourcingHandler
    fun evolve(event: EligibilityDetermined) {
        this.isEligibleForPrize = event.isEligible
        if (event.isEligible) {
            this.challengeStatus = "COMPLETED"
        }
    }

    /**
     * Handles ProjectSharedToGallery event to track project sharing completion
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: ProjectSharedToGallery) {
        this.projectShared = true
    }

    companion object {
        /**
         * Builds EventCriteria for loading participant challenge state events
         */
        @EventCriteriaBuilder
        fun resolveCriteria(participantId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Participant", participantId))
                .andBeingOneOfTypes(
                    ChallengeStarted::class.java.name,
                    VoteRegistered::class.java.name,
                    ApplicationGeneratedSuccessfully::class.java.name,
                    EligibilityDetermined::class.java.name,
                    ProjectSharedToGallery::class.java.name
                )
        }
    }
}