package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog

import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Gallery Catalog component providing HTTP endpoints for gallery project queries.
 * Exposes endpoints to retrieve completed applications, gallery projects list, and project details.
 */
@RestController
@RequestMapping("/api/gallery-catalog")
class GalleryCatalogController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(GalleryCatalogController::class.java)
    }

    /**
     * GET endpoint to retrieve completed application data for a specific participant.
     * Maps to CompletedApplication query handler in the Gallery Catalog component.
     */
    @GetMapping("/completed-applications/{participantId}")
    fun getCompletedApplication(@PathVariable participantId: String): CompletableFuture<CompletedApplicationData> {
        logger.info("REST request for completed application, participantId: $participantId")
        val query = CompletedApplication(participantId)
        return queryGateway.query(query, CompletedApplicationData::class.java, null)
    }

    /**
     * GET endpoint to retrieve list of all gallery projects.
     * Maps to GalleryProjects query handler in the Gallery Catalog component.
     */
    @GetMapping("/projects")
    fun getGalleryProjects(): CompletableFuture<GalleryProjectsList> {
        logger.info("REST request for gallery projects list")
        val query = GalleryProjects()
        return queryGateway.query(query, GalleryProjectsList::class.java, null)
    }

    /**
     * GET endpoint to retrieve detailed information about a specific project.
     * Maps to ProjectDetails query handler in the Gallery Catalog component.
     */
    @GetMapping("/projects/{projectId}")
    fun getProjectDetails(@PathVariable projectId: String): CompletableFuture<ProjectDetailsData> {
        logger.info("REST request for project details, projectId: $projectId")
        val query = ProjectDetails(projectId)
        return queryGateway.query(query, ProjectDetailsData::class.java, null)
    }
}