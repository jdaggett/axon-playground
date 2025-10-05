package io.axoniq.build.apex_racing_labs.race_management

import io.axoniq.build.apex_racing_labs.race_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * Race Management Service REST Controller
 * Exposes REST endpoints for race creation and cancellation functionality.
 */
@RestController
@RequestMapping("/api/race-management")
class RaceManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceManagementController::class.java)
    }

    /**
     * Creates a new race.
     * 
     * @param request The race creation request containing race details
     * @return ResponseEntity with success or error message
     */
    @PostMapping("/races")
    fun createRace(@RequestBody request: CreateRaceRequest): ResponseEntity<String> {
        val command = CreateRace(
            raceId = request.raceId,
            participatingDriverIds = request.participatingDriverIds,
            raceDate = request.raceDate,
            trackName = request.trackName
        )

        logger.info("Dispatching CreateRace command: {}", command)

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Race creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateRace command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create race")
        }
    }

    /**
     * Cancels an existing race.
     * 
     * @param raceId The ID of the race to cancel
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/races/{raceId}")
    fun cancelRace(@PathVariable raceId: String): ResponseEntity<String> {
        val command = CancelRace(raceId = raceId)

        logger.info("Dispatching CancelRace command: {}", command)

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Race cancellation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CancelRace command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel race")
        }
    }

    /**
     * Data class for race creation request payload.
     */
    data class CreateRaceRequest(
        val raceId: String,
        val participatingDriverIds: List<String>,
        val raceDate: LocalDate,
        val trackName: String
    )
}

