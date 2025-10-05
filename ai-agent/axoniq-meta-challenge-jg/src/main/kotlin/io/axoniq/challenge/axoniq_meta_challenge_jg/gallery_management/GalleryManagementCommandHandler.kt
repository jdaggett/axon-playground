package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.exception.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.*

/**
 * Gallery Management Command Handler - Handles project sharing and voting operations
 * 
 * This component manages the core gallery functionality including:
 * - Project submission and sharing to the gallery
 * - Voting system for projects in the gallery
 * - Validation of participant actions and state transitions
 */
class GalleryManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GalleryManagementCommandHandler::class.java)
    }

    /**
     * Handles ShareProjectToGallery command
     * 
     * Validates the project sharing request and publishes the project to the gallery.
     * Ensures participants can only share one project and handles sharing failures.
     *
     * @param command The project sharing command containing application details
     * @param state Current gallery management state for the participant
     * @param eventAppender Event appender for publishing events
     * @return ProjectSharingResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: ShareProjectToGallery,
        @InjectEntity state: GalleryManagementState,
        eventAppender: EventAppender
    ): ProjectSharingResult {
        logger.info("Handling ShareProjectToGallery command: participantId={}, projectTitle={}", 
                   command.participantId, command.projectTitle)

        try {
            // Validate that participant hasn't already shared a project
            if (state.getHasSharedProject()) {
                logger.error("Project sharing failed: Participant {} has already shared a project", command.participantId)
                throw GallerySharingFailed("Participant has already shared a project to the gallery")
            }

            // Generate unique project ID for the shared project
            val projectId = UUID.randomUUID().toString()

            // Create and append project shared event
            val event = ProjectSharedToGallery(
                submissionTime = LocalDateTime.now(),
                participantId = command.participantId,
                projectTitle = command.projectTitle,
                projectId = projectId
            )
            
            eventAppender.append(event)

            logger.info("Project successfully shared to gallery: projectId={}, participantId={}", 
                       projectId, command.participantId)

            return ProjectSharingResult(
                projectId = projectId,
                isSuccessful = true
            )

        } catch (ex: GallerySharingFailed) {
            logger.error("Gallery sharing failed for participant {}: {}", command.participantId, ex.message)
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error during project sharing for participant {}", command.participantId, ex)
            throw GallerySharingFailed("Failed to share project to gallery: ${ex.message}")
        }
    }

    /**
     * Handles VoteForProject command
     * 
     * Processes voting requests for projects in the gallery. Validates voting eligibility
     * and prevents duplicate votes on the same project by the same participant.
     * 
     * @param command The voting command containing vote details
     * @param state Current gallery management state for the participant
     * @param eventAppender Event appender for publishing events
     * @return VotingResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: VoteForProject,
        @InjectEntity state: GalleryManagementState,
        eventAppender: EventAppender
    ): VotingResult {
        logger.info("Handling VoteForProject command: participantId={}, projectId={}, voteType={}",
                   command.participantId, command.projectId, command.voteType)
        
        try {
            // Validate that participant hasn't already voted for this project
            if (state.getVotedProjectIds().contains(command.projectId)) {
                logger.error("Voting failed: Participant {} has already voted for project {}", 
                           command.participantId, command.projectId)
                throw VotingSystemError("Participant has already voted for this project")
            }
            
            // Simulate potential network issues during voting
            if (Math.random() < 0.1) { // 10% chance of network failure
                logger.error("Internet connection lost during voting for participant {}", command.participantId)
                throw InternetConnectionLost("Network connection lost while processing vote")
            }
            
            // Create and append vote registered event
            val event = VoteRegistered(
                participantId = command.participantId,
                voteType = command.voteType,
                projectId = command.projectId
            )
            
            eventAppender.append(event)

            logger.info("Vote successfully registered: participantId={}, projectId={}, voteType={}", 
                       command.participantId, command.projectId, command.voteType)

            return VotingResult(isSuccessful = true)
            
        } catch (ex: InternetConnectionLost) {
            logger.error("Internet connection lost during voting: {}", ex.message)
            throw ex
        } catch (ex: VotingSystemError) {
            logger.error("Voting system error: {}", ex.message)
            throw ex
        } catch (ex: Exception) {
            logger.error("Unexpected error during voting for participant {}", command.participantId, ex)
            throw VotingSystemError("Voting system encountered an error: ${ex.message}")
        }
    }
}

