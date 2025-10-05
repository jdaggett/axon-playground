package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for Gallery Catalog component to manage gallery project entities.
 * Extends JpaRepository to provide basic CRUD operations for GalleryProject entities.
 */
@Repository
interface GalleryProjectRepository : JpaRepository<GalleryProject, String> {

    /**
     * Finds a gallery project by participant ID.
     * Used for handling CompletedApplication queries.
     */
    fun findByParticipantId(participantId: String): GalleryProject?
}

