package io.axoniq.build.apex_racing_labs.teams_catalog_view

import io.axoniq.build.apex_racing_labs.teams_catalog_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Teams Catalog View component providing available teams information.
 * Handles queries for team details and available teams, and maintains the read model
 * by listening to team creation and removal events.
 */
@Component
class TeamsCatalogViewComponent(
    private val teamRepository: TeamRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TeamsCatalogViewComponent::class.java)
    }

    /**
     * Query handler for TeamDetails query.
     * Retrieves detailed information about a specific team.
     * 
     * @param query The TeamDetails query containing the team ID
     * @return TeamDetailsResult with team information
     */
    @QueryHandler
    fun handle(query: TeamDetails): TeamDetailsResult {
        logger.info("Processing TeamDetails query for teamId: {}", query.teamId)

        val team = teamRepository.findById(query.teamId)
            .orElseThrow { IllegalArgumentException("Team not found with id: ${query.teamId}") }

        return TeamDetailsResult(
            teamId = team.teamId,
            active = team.active,
            teamName = team.teamName
        )
    }

    /**
     * Query handler for AvailableTeams query.
     * Retrieves all available (active) teams.
     * 
     * @param query The AvailableTeams query
     * @return AvailableTeamsResult containing list of available teams
     */
    @QueryHandler
    fun handle(query: AvailableTeams): AvailableTeamsResult {
        logger.info("Processing AvailableTeams query")

        val activeTeams = teamRepository.findAllActiveTeams()
        val teamInfoList = activeTeams.map { team ->
            TeamInfo(
                teamId = team.teamId,
                teamName = team.teamName
            )
        }

        return AvailableTeamsResult(teams = teamInfoList)
    }

    /**
     * Event handler for TeamCreated event.
     * Creates a new team entry in the read model when a team is created.
     * 
     * @param event The TeamCreated event
     */
    @EventHandler
    fun on(event: TeamCreated) {
        logger.info("Processing TeamCreated event for teamId: {}", event.teamId)

        val team = Team(
            teamId = event.teamId,
            active = true,
            teamName = event.teamName
        )

        teamRepository.save(team)
        logger.info("Team created in read model: {}", event.teamId)
    }

    /**
     * Event handler for TeamRemoved event.
     * Marks a team as inactive in the read model when a team is removed.
     * 
     * @param event The TeamRemoved event
     */
    @EventHandler
    fun on(event: TeamRemoved) {
        logger.info("Processing TeamRemoved event for teamId: {}", event.teamId)
        
        val existingTeam = teamRepository.findById(event.teamId)
        if (existingTeam.isPresent) {
            val team = existingTeam.get()
            val updatedTeam = team.copy(active = false)
            teamRepository.save(updatedTeam)
            logger.info("Team marked as inactive in read model: {}", event.teamId)
        } else {
            logger.warn("Team not found for removal: {}", event.teamId)
        }
    }
}

