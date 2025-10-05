package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard

import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

/**
 * JPA Entity representing the Challenge Dashboard view model.
 * Tracks participant progress and challenge information for the Challenge Dashboard component.
 */
@Entity
@Table(name = "challenge_dashboard")
data class ChallengeDashboardEntity(
    @Id
    @Column(name = "participant_id")
    val participantId: String = "",

    @Column(name = "completion_percentage")
    val completionPercentage: Int = 0,

    @Column(name = "vote_cast")
    val voteCast: Boolean = false,

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "step_instructions")
    val stepInstructions: List<String> = emptyList(),

    @Column(name = "application_created")
    val applicationCreated: Boolean = false,

    @Column(name = "is_eligible")
    val isEligible: Boolean = false,

    @Column(name = "project_shared")
    val projectShared: Boolean = false,

    @Column(name = "challenge_started")
    val challengeStarted: Boolean = false
)

