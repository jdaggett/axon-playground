package io.axoniq.build.apex_racing_labs.team_performance_view

import io.axoniq.build.apex_racing_labs.team_performance_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Team Performance Statistics View component.
 * Exposes HTTP endpoints to query team performance analytics and statistics.
 */
@RestController
@RequestMapping("/api/team-performance")
class TeamPerformanceViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TeamPerformanceViewController::class.java)
    }

    /**
     * Retrieves team performance statistics for a specific team.
     * Returns comprehensive performance data including total races, average rating, and best race performances.
     */
    @GetMapping("/{teamId}/statistics")
    fun getTeamPerformanceStatistics(@PathVariable teamId: String): CompletableFuture<TeamPerformanceResult> {
        logger.info("Received request for team performance statistics: $teamId")

        val query = TeamPerformanceStatistics(teamId = teamId)
        return queryGateway.query(query, TeamPerformanceResult::class.java, null)
    }
}