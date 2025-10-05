package io.axoniq.build.apex_racing_labs.teams_catalog_view

import io.axoniq.build.apex_racing_labs.teams_catalog_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Teams Catalog View component.
 * Exposes HTTP endpoints for querying team information.
 */
@RestController
@RequestMapping("/api/teams")
class TeamsCatalogViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TeamsCatalogViewController::class.java)
    }

    /**
     * REST endpoint to get details of a specific team.
     *
     * @param teamId The ID of the team to retrieve
     * @return CompletableFuture containing team details
     */
    @GetMapping("/{teamId}")
    fun getTeamDetails(@PathVariable teamId: String): CompletableFuture<TeamDetailsResult> {
        logger.info("REST request for team details: {}", teamId)
        val query = TeamDetails(teamId)
        return queryGateway.query(query, TeamDetailsResult::class.java, null)
    }

    /**
     * REST endpoint to get all available teams.
     * 
     * @return CompletableFuture containing list of available teams
     */
    @GetMapping("/available")
    fun getAvailableTeams(): CompletableFuture<AvailableTeamsResult> {
        logger.info("REST request for available teams")
        val query = AvailableTeams()
        return queryGateway.query(query, AvailableTeamsResult::class.java, null)
    }
}