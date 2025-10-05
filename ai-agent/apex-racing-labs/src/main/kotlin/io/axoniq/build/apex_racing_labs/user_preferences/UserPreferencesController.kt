package io.axoniq.build.apex_racing_labs.user_preferences

import io.axoniq.build.apex_racing_labs.user_preferences.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * UserPreferencesController - REST controller for the User Preferences Service component.
 * Provides HTTP endpoints for managing user favorite driver and team selections.
 */
@RestController
@RequestMapping("/api/user-preferences")
class UserPreferencesController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserPreferencesController::class.java)
    }

    /**
     * Endpoint to select a favorite team for a user.
     */
    @PostMapping("/favorite-team")
    fun selectFavoriteTeam(@RequestBody request: SelectFavoriteTeamRequest): ResponseEntity<String> {
        val command = SelectFavoriteTeam(
            teamId = request.teamId,
            userId = request.userId
        )
        logger.info("Dispatching SelectFavoriteTeam command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Favorite team selection accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SelectFavoriteTeam command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to select favorite team")
        }
    }

    /**
     * Endpoint to select a favorite driver for a user.
     */
    @PostMapping("/favorite-driver")
    fun selectFavoriteDriver(@RequestBody request: SelectFavoriteDriverRequest): ResponseEntity<String> {
        val command = SelectFavoriteDriver(
            userId = request.userId,
            driverId = request.driverId
        )
        logger.info("Dispatching SelectFavoriteDriver command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Favorite driver selection accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SelectFavoriteDriver command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to select favorite driver")
        }
    }
}

/**
 * Request DTO for selecting favorite team
 */
data class SelectFavoriteTeamRequest(
    val teamId: String,
    val userId: String
)

/**
 * Request DTO for selecting favorite driver
 */
data class SelectFavoriteDriverRequest(
    val userId: String,
    val driverId: String
)

