package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_catalog

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA Entity representing a gallery project in the Gallery Catalog view component.
 * This entity stores project information including submission details, creator info, and voting data.
 */
@Entity
@Table(name = "gallery_projects")
data class GalleryProject(
    @Id
    @Column(name = "project_id")
    val projectId: String = "",

    @Column(name = "submission_time", nullable = false)
    val submissionTime: LocalDateTime = LocalDateTime.now(),

    @Column(name = "participant_id", nullable = false)
    val participantId: String = "",

    @Column(name = "creator_name", nullable = false)
    val creatorName: String = "",
    
    @Column(name = "project_title", nullable = false)
    val projectTitle: String = "",

    @Column(name = "vote_count", nullable = false)
    val voteCount: Int = 0,

    @Column(name = "application_id", nullable = true)
    val applicationId: String? = null
)

