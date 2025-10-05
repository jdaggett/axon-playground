package io.axoniq.build.apex_racing_labs.user_setup

import io.axoniq.build.apex_racing_labs.user_setup.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for User Setup Service component.
 * Handles user initial setup with preferences, allowing users to complete their initial configuration
 * by specifying their favorite team and driver preferences.
 */
class UserSetupServiceCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserSetupServiceCommandHandler::class.java)
    }

    /**
     * Handles the CompleteInitialSetup command for the User Setup Service component.
     * Validates that the user setup hasn't been completed yet and processes the initial setup
     * with the user's favorite team and driver preferences.
     *
     * @param command The CompleteInitialSetup command containing user ID and preferences
     * @param state The current UserSetupServiceState for validation
     * @param eventAppender The event appender to publish events
     * @return UserSetupResult indicating success or failure of the setup completion
     */
    @CommandHandler
    fun handle(
        command: CompleteInitialSetup,
        @InjectEntity state: UserSetupServiceState,
        eventAppender: EventAppender
    ): UserSetupResult {
        logger.info("Handling CompleteInitialSetup command for user: ${command.userId}")

        // Validate that setup hasn't been completed yet
        if (state.getSetupCompleted()) {
            logger.warn("User setup already completed for user: ${command.userId}")
            return UserSetupResult(
                success = false,
                message = "User setup has already been completed"
            )
        }

        // Create and append the UserSetupCompleted event
        val event = UserSetupCompleted(
            userId = command.userId,
            favoriteTeamId = command.favoriteTeamId,
            favoriteDriverId = command.favoriteDriverId
        )

        logger.info("Publishing UserSetupCompleted event for user: ${command.userId}")
        eventAppender.append(event)

        return UserSetupResult(
            success = true,
            message = "User setup completed successfully"
        )
    }
}

