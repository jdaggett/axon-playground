package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard

import io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Admin Dashboard - exposes administrative endpoints
 * for AxonIQ employees to access challenge, participant, and prize data
 */
@RestController
@RequestMapping("/api/admin-dashboard")
class AdminDashboardController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AdminDashboardController::class.java)
    }
    
    /**
     * Get prize collection instructions for a specific participant
     */
    @GetMapping("/prize-instructions/{participantId}")
    fun getPrizeInstructions(@PathVariable participantId: String): CompletableFuture<PrizeInstructionsData> {
        logger.info("Getting prize instructions for participant: $participantId")
        val query = PrizeCollectionInstructions(participantId)
        return queryGateway.query(query, PrizeInstructionsData::class.java, null)
    }
    
    /**
     * Get detailed completion data for a specific participant
     */
    @GetMapping("/completion-details/{participantId}")
    fun getCompletionDetails(@PathVariable participantId: String): CompletableFuture<DetailedCompletionData> {
        logger.info("Getting completion details for participant: $participantId")
        val query = DetailedChallengeCompletion(participantId)
        return queryGateway.query(query, DetailedCompletionData::class.java, null)
    }

    /**
     * Get all running challenges
     */
    @GetMapping("/running-challenges")
    fun getRunningChallenges(): CompletableFuture<AllRunningChallengesData> {
        logger.info("Getting all running challenges")
        val query = AllRunningChallenges()
        return queryGateway.query(query, AllRunningChallengesData::class.java, null)
    }
    
    /**
     * Get participant results dashboard data
     */
    @GetMapping("/participant-results")
    fun getParticipantResults(): CompletableFuture<ParticipantResultsData> {
        logger.info("Getting participant results dashboard")
        val query = ParticipantResultsDashboard()
        return queryGateway.query(query, ParticipantResultsData::class.java, null)
    }
}

