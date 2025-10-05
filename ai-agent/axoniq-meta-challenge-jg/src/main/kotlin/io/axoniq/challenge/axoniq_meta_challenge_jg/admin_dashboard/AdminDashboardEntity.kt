package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA Entity for Admin Dashboard - stores administrative data for managing challenges,
 * participants, and prizes for AxonIQ employees
 */
@Entity
@Table(name = "admin_dashboard")
data class AdminDashboardEntity(
    @Id
    val participantId: String,
    
    @Column(nullable = false)
    val challengeId: String,

    @Column
    val completionTime: LocalDateTime? = null,

    @Column(nullable = false)
    val isWinner: Boolean = false,

    @Column(nullable = false)
    val challengeStatus: String,

    @Column(nullable = false)
    val participantEmail: String,

    @Column(nullable = false)
    val isEligible: Boolean = false,

    @Column(nullable = false)
    val prizeClaimed: Boolean = false,

    @Column
    val startTime: LocalDateTime? = null
) {
    // Default constructor for JPA
    constructor() : this(
        participantId = "",
        challengeId = "",
        completionTime = null,
        isWinner = false,
        challengeStatus = "",
        participantEmail = "",
        isEligible = false,
        prizeClaimed = false,
        startTime = null
    )
}

