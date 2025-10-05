package io.axoniq.build.apex_racing_labs.season_standings_view

import io.axoniq.build.apex_racing_labs.season_standings_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for exposing Season Standings View endpoints.
 * Provides HTTP access to season team standings data through the query gateway.
 */
@RestController
@RequestMapping("/api/season-standings")
class SeasonStandingsViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(SeasonStandingsViewController::class.java)
    }

    /**
     * Gets the current season team standings with positions and average ratings.
     * Returns a list of teams ordered by their current standings position.
     */
    @GetMapping
    fun getSeasonStandings(): CompletableFuture<SeasonStandingsResult> {
        logger.info("REST request for season team standings")

        val query = SeasonTeamStandings()
        return queryGateway.query(query, SeasonStandingsResult::class.java, null)
            .thenApply { result ->
                logger.debug("Retrieved season standings with {} teams", result.standings.size)
                result
            }
    }
}