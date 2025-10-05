package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog

import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Gallery Catalog query component that provides gallery project listing and detailed project information.
 * This component handles queries for completed applications, gallery projects list, and project details.
 * It also processes events to maintain the read model for gallery projects.
 */
@Component
class GalleryCatalogQueryComponent(
    private val galleryProjectRepository: GalleryProjectRepository
) {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GalleryCatalogQueryComponent::class.java)
    }

    /**
     * Handles CompletedApplication query to retrieve completed application data for a participant.
     * Returns application details including title, ID, and sharing readiness status.
     */
    @QueryHandler
    fun handle(query: CompletedApplication): CompletedApplicationData? {
        logger.info("Handling CompletedApplication query for participantId: ${query.participantId}")

        val project = galleryProjectRepository.findByParticipantId(query.participantId)

        return project?.let {
            CompletedApplicationData(
                applicationTitle = it.projectTitle,
                applicationId = it.applicationId ?: it.projectId,
                isReadyForSharing = it.applicationId != null
            )
        }
    }

    /**
     * Handles GalleryProjects query to retrieve list of all projects in the gallery.
     * Returns a list of project summaries sorted by submission time.
     */
    @QueryHandler
    fun handle(query: GalleryProjects): GalleryProjectsList {
        logger.info("Handling GalleryProjects query to retrieve all gallery projects")

        val projects = galleryProjectRepository.findAll()
            .sortedByDescending { it.submissionTime }
            .map { project ->
                ProjectSummary(
                    submissionTime = project.submissionTime,
                    creatorName = project.creatorName,
                    projectTitle = project.projectTitle,
                    projectId = project.projectId
                )
            }

        logger.debug("Retrieved ${projects.size} gallery projects")
        return GalleryProjectsList(projects)
    }
    
    /**
     * Handles ProjectDetails query to retrieve detailed information about a specific project.
     * Returns comprehensive project data including vote count and submission details.
     */
    @QueryHandler
    fun handle(query: ProjectDetails): ProjectDetailsData? {
        logger.info("Handling ProjectDetails query for projectId: ${query.projectId}")
        
        val project = galleryProjectRepository.findById(query.projectId).orElse(null)
        
        return project?.let {
            ProjectDetailsData(
                submissionTime = it.submissionTime,
                creatorName = it.creatorName,
                projectTitle = it.projectTitle,
                projectId = it.projectId,
                voteCount = it.voteCount
            )
        }
    }

    /**
     * Event handler for VoteRegistered events to update vote counts for gallery projects.
     * Increments the vote count when a vote is registered for a project.
     */
    @EventHandler
    fun on(event: VoteRegistered) {
        logger.info("Handling VoteRegistered event for projectId: ${event.projectId}, voteType: ${event.voteType}")

        val project = galleryProjectRepository.findById(event.projectId).orElse(null)

        project?.let {
            val updatedProject = it.copy(voteCount = it.voteCount + 1)
            galleryProjectRepository.save(updatedProject)
            logger.debug("Updated vote count for project ${event.projectId} to ${updatedProject.voteCount}")
        } ?: logger.warn("Project with ID ${event.projectId} not found for vote registration")
    }
    
    /**
     * Event handler for ProjectSharedToGallery events to create or update gallery project entries.
     * Creates new gallery project records when projects are shared to the gallery.
     */
    @EventHandler
    fun on(event: ProjectSharedToGallery) {
        logger.info("Handling ProjectSharedToGallery event for projectId: ${event.projectId}")

        val existingProject = galleryProjectRepository.findById(event.projectId).orElse(null)
        
        if (existingProject == null) {
            val newProject = GalleryProject(
                projectId = event.projectId,
                submissionTime = event.submissionTime,
                participantId = event.participantId,
                creatorName = "", // Will be updated when participant details are available
                projectTitle = event.projectTitle,
                voteCount = 0,
                applicationId = event.projectId
            )
            galleryProjectRepository.save(newProject)
            logger.debug("Created new gallery project entry for projectId: ${event.projectId}")
        } else {
            val updatedProject = existingProject.copy(
                submissionTime = event.submissionTime,
                projectTitle = event.projectTitle,
                applicationId = event.projectId
            )
            galleryProjectRepository.save(updatedProject)
            logger.debug("Updated existing gallery project entry for projectId: ${event.projectId}")
        }
    }
}

