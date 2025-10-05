package io.axoniq.build.apex_racing_labs.user_statistics_view

import io.axoniq.build.apex_racing_labs.user_statistics_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the User Statistics View component.
 * Provides HTTP endpoints for accessing personal vs community rating statistics.
 */
@RestController
@RequestMapping("/api/user-statistics")
class UserStatisticsViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserStatisticsViewController::class.java)
    }

    /**
     * Gets personal vs community statistics for a specific user.
     * Returns comprehensive rating comparisons and averages.
     * @param userId the user ID to get statistics for
     * @return CompletableFuture containing UserStatisticsResult
     */
    @GetMapping("/{userId}")
    fun getUserStatistics(@PathVariable userId: String): CompletableFuture<UserStatisticsResult> {
        logger.info("REST request for user statistics: $userId")
        val query = PersonalVsCommunityStatistics(userId)
        return queryGateway.query(query, UserStatisticsResult::class.java, null)
    }
}