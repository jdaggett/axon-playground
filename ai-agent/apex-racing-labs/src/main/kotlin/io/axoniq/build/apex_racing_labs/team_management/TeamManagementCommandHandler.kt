package io.axoniq.build.apex_racing_labs.team_management

import io.axoniq.build.apex_racing_labs.team_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for Team Management Service component.
 * Handles team creation and removal commands.
 */
class TeamManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TeamManagementCommandHandler::class.java)
    }

    /**
     * Command handler for CreateTeam command.
     * Creates a new team if it doesn't already exist.
     */
    @CommandHandler
    fun handle(
        command: CreateTeam,
        @InjectEntity state: TeamManagementState,
        eventAppender: EventAppender
    ): TeamCreationResult {
        logger.info("Handling CreateTeam command for team ID: ${command.teamId}")

        // Check if team already exists and is active
        if (state.getActive()) {
            val message = "Team with ID ${command.teamId} already exists and is active"
            logger.warn(message)
            return TeamCreationResult(success = false, message = message)
        }

        // Create the team
        val event = TeamCreated(
            teamId = command.teamId,
            teamName = command.teamName
        )

        eventAppender.append(event)
        logger.info("Team created successfully with ID: ${command.teamId}")

        return TeamCreationResult(
            success = true,
            message = "Team created successfully"
        )
    }

    /**
     * Command handler for RemoveTeam command.
     * Removes an existing active team.
     */
    @CommandHandler
    fun handle(
        command: RemoveTeam,
        @InjectEntity state: TeamManagementState,
        eventAppender: EventAppender
    ): TeamRemovalResult {
        logger.info("Handling RemoveTeam command for team ID: ${command.teamId}")

        // Check if team exists and is active
        if (!state.getActive()) {
            val message = "Team with ID ${command.teamId} does not exist or is already removed"
            logger.warn(message)
            return TeamRemovalResult(success = false, message = message)
        }

        // Remove the team
        val event = TeamRemoved(teamId = command.teamId)

        eventAppender.append(event)
        logger.info("Team removed successfully with ID: ${command.teamId}")

        return TeamRemovalResult(
            success = true,
            message = "Team removed successfully"
        )
    }
}

