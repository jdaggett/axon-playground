package io.axoniq.build.apex_racing_labs.user_preferences

import io.axoniq.build.apex_racing_labs.user_preferences.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * UserPreferencesCommandHandler - Command handler for the User Preferences Service component.
 * Handles user favorite driver and team selections.
 */
class UserPreferencesCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserPreferencesCommandHandler::class.java)
    }

    /**
     * Handles SelectFavoriteTeam command.
     * Allows a user to select their favorite racing team.
     */
    @CommandHandler
    fun handle(
        command: SelectFavoriteTeam,
        @InjectEntity state: UserPreferencesState,
        eventAppender: EventAppender
    ): FavoriteTeamResult {
        logger.info("Handling SelectFavoriteTeam command for user ${command.userId} with team ${command.teamId}")

        try {
            val event = FavoriteTeamSelected(
                teamId = command.teamId,
                userId = command.userId
            )

            eventAppender.append(event)
            logger.info("Successfully selected favorite team ${command.teamId} for user ${command.userId}")

            return FavoriteTeamResult(
                success = true,
                message = "Favorite team selected successfully"
            )
        } catch (e: Exception) {
            logger.error("Failed to select favorite team for user ${command.userId}", e)
            return FavoriteTeamResult(
                success = false,
                message = "Failed to select favorite team: ${e.message}"
            )
        }
    }

    /**
     * Handles SelectFavoriteDriver command.
     * Allows a user to select their favorite racing driver.
     */
    @CommandHandler
    fun handle(
        command: SelectFavoriteDriver,
        @InjectEntity state: UserPreferencesState,
        eventAppender: EventAppender
    ): FavoriteDriverResult {
        logger.info("Handling SelectFavoriteDriver command for user ${command.userId} with driver ${command.driverId}")

        try {
            val event = FavoriteDriverSelected(
                userId = command.userId,
                driverId = command.driverId
            )

            eventAppender.append(event)
            logger.info("Successfully selected favorite driver ${command.driverId} for user ${command.userId}")

            return FavoriteDriverResult(
                success = true,
                message = "Favorite driver selected successfully"
            )
        } catch (e: Exception) {
            logger.error("Failed to select favorite driver for user ${command.userId}", e)
            return FavoriteDriverResult(
                success = false,
                message = "Failed to select favorite driver: ${e.message}"
            )
        }
    }
}

