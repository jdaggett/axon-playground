package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Gallery Management REST Controller
 * 
 * Exposes REST endpoints for gallery management operations including
 * project sharing and voting functionality. Routes HTTP requests to
 * appropriate command handlers via the CommandGateway.
 */
@RestController
@RequestMapping("/api/gallery")
class GalleryManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GalleryManagementController::class.java)
    }

    /**
     * Shares a project to the gallery
     *
     * @param request Project sharing request containing participant and project details
     * @return ResponseEntity indicating success or failure of the sharing operation
     */
    @PostMapping("/share")
    fun shareProject(@RequestBody request: ShareProjectToGalleryRequest): ResponseEntity<String> {
        val command = ShareProjectToGallery(
            applicationId = request.applicationId,
            participantId = request.participantId,
            projectTitle = request.projectTitle
        )
        
        logger.info("Dispatching ShareProjectToGallery command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Project sharing request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ShareProjectToGallery command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to share project to gallery")
        }
    }
    
    /**
     * Submits a vote for a project
     * 
     * @param request Voting request containing participant, project, and vote details
     * @return ResponseEntity indicating success or failure of the voting operation
     */
    @PostMapping("/vote")
    fun voteForProject(@RequestBody request: VoteForProjectRequest): ResponseEntity<String> {
        val command = VoteForProject(
            participantId = request.participantId,
            voteType = request.voteType,
            projectId = request.projectId
        )
        
        logger.info("Dispatching VoteForProject command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Vote submission accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch VoteForProject command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to submit vote")
        }
    }
}

/**
 * Request model for project sharing endpoint
 */
data class ShareProjectToGalleryRequest(
    val applicationId: String,
    val participantId: String,
    val projectTitle: String
)

/**
 * Request model for voting endpoint
 */
data class VoteForProjectRequest(
    val participantId: String,
    val voteType: String,
    val projectId: String
)

