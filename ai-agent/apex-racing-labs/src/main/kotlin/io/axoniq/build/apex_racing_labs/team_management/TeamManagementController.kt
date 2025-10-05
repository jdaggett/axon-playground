package io.axoniq.build.apex_racing_labs.team_management

import io.axoniq.build.apex_racing_labs.team_management.api.CreateTeam
import io.axoniq.build.apex_racing_labs.team_management.api.RemoveTeam
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Team Management Service component.
 * Exposes endpoints for team creation and removal operations.
 */
@RestController
@RequestMapping("/api/teams")
class TeamManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TeamManagementController::class.java)
    }

    /**
     * Creates a new team.
     * @param request The team creation request containing team details
     * @return Response indicating success or failure
     */
    @PostMapping
    fun createTeam(@RequestBody request: CreateTeamRequest): ResponseEntity<String> {
        val command = CreateTeam(
            teamId = request.teamId,
            teamName = request.teamName
        )

        logger.info("Dispatching CreateTeam command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Team creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateTeam command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create team")
        }
    }

    /**
     * Removes an existing team.
     * @param teamId The ID of the team to remove
     * @return Response indicating success or failure
     */
    @DeleteMapping("/{teamId}")
    fun removeTeam(@PathVariable teamId: String): ResponseEntity<String> {
        val command = RemoveTeam(teamId = teamId)

        logger.info("Dispatching RemoveTeam command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Team removal accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RemoveTeam command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove team")
        }
    }

    /**
     * Request data class for team creation endpoint.
     */
    data class CreateTeamRequest(
        val teamId: String,
        val teamName: String
    )
}

