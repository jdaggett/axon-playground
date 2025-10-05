package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for AdminDashboardEntity - provides data access methods
 * for administrative queries and operations
 */
@Repository
interface AdminDashboardRepository : JpaRepository<AdminDashboardEntity, String> {

    /**
     * Find all eligible participants ordered by completion time
     */
    fun findByIsEligibleTrueOrderByCompletionTimeAsc(): List<AdminDashboardEntity>

    /**
     * Find participant by participant ID
     */
    override fun findById(participantId: String): java.util.Optional<AdminDashboardEntity>

    /**
     * Find all running challenges
     */
    @Query("SELECT DISTINCT a.challengeId FROM AdminDashboardEntity a WHERE a.challengeStatus = 'RUNNING'")
    fun findRunningChallengeIds(): List<String>
}

