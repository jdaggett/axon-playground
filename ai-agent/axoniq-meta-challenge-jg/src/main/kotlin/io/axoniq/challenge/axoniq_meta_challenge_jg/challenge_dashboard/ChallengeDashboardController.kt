package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard

import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Challenge Dashboard endpoints.
 * Provides HTTP access to challenge overview and participant progress data.
 */
@RestController
@RequestMapping("/api/challenge-dashboard")
class ChallengeDashboardController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChallengeDashboardController::class.java)
    }

    /**
     * Retrieves general challenge overview information.
     * Returns challenge title, requirements, and estimated completion time.
     */
    @GetMapping("/overview")
    fun getChallengeOverview(): CompletableFuture<ChallengeOverviewData> {
        logger.info("Received request for challenge overview")
        val query = ChallengeOverview()
        return queryGateway.query(query, ChallengeOverviewData::class.java, null)
    }

    /**
     * Retrieves progress dashboard data for a specific participant.
     * Returns detailed progress information including completion percentage and milestone status.
     */
    @GetMapping("/progress/{participantId}")
    fun getProgressDashboard(@PathVariable participantId: String): CompletableFuture<ProgressDashboardData> {
        logger.info("Received request for progress dashboard for participant: $participantId")
        val query = ProgressDashboard(participantId)
        return queryGateway.query(query, ProgressDashboardData::class.java, null)
    }
}