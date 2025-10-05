package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Gallery Management State - Event-sourced entity for the Gallery Management component
 *
 * This state tracks participant activities in the gallery including project sharing
 * and voting behavior. It maintains the current state of a participant's interactions
 * with the gallery system including their voting history and project sharing status.
 */
@EventSourcedEntity
class GalleryManagementState {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GalleryManagementState::class.java)

        /**
         * Builds event criteria for loading Gallery Management state
         * Uses participant tag to load all relevant events for a specific participant
         */
        @EventCriteriaBuilder
        fun resolveCriteria(participantId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Participant", participantId))
                .andBeingOneOfTypes(
                    ProjectSharedToGallery::class.java.name,
                    VoteRegistered::class.java.name
                )
        }
    }

    private var participantId: String? = null
    private var projectId: String? = null
    private var hasVoted: Boolean = false
    private var votedProjectIds: MutableList<String> = mutableListOf()
    private var hasSharedProject: Boolean = false

    // Getters for state properties
    fun getParticipantId(): String? = participantId
    fun getProjectId(): String? = projectId
    fun getHasVoted(): Boolean = hasVoted
    fun getVotedProjectIds(): List<String> = votedProjectIds.toList()
    fun getHasSharedProject(): Boolean = hasSharedProject

    @EntityCreator
    constructor()

    /**
     * Evolves state when a project is shared to the gallery
     * Updates participant tracking and project sharing status
     */
    @EventSourcingHandler
    fun evolve(event: ProjectSharedToGallery) {
        logger.debug("Evolving state for ProjectSharedToGallery event: participantId={}, projectId={}",
                    event.participantId, event.projectId)

        this.participantId = event.participantId
        this.projectId = event.projectId
        this.hasSharedProject = true
    }

    /**
     * Evolves state when a vote is registered
     * Updates voting status and tracks voted project IDs
     */
    @EventSourcingHandler
    fun evolve(event: VoteRegistered) {
        logger.debug("Evolving state for VoteRegistered event: participantId={}, projectId={}, voteType={}", 
                    event.participantId, event.projectId, event.voteType)

        this.participantId = event.participantId
        this.hasVoted = true
        this.votedProjectIds.add(event.projectId)
    }
}