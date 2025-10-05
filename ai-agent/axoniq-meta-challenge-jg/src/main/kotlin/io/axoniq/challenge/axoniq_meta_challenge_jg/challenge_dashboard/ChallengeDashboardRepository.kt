package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for Challenge Dashboard entities.
 * Provides data access operations for the Challenge Dashboard component.
 */
interface ChallengeDashboardRepository : JpaRepository<ChallengeDashboardEntity, String>

