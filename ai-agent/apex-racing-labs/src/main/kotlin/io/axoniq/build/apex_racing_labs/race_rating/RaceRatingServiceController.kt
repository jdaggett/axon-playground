package io.axoniq.build.apex_racing_labs.race_rating

import io.axoniq.build.apex_racing_labs.race_rating.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Race Rating Service operations.
 * Provides endpoints for rating races through HTTP requests.
 */
@RestController
@RequestMapping("/api/race-rating")
class RaceRatingServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceRatingServiceController::class.java)
    }

    /**
     * Endpoint to rate a race.
     *
     * @param request The rate race request containing race ID, user ID, rating and optional comment
     * @return ResponseEntity indicating success or failure
     */
    @PostMapping("/rate")
    fun rateRace(@RequestBody request: RateRaceRequest): ResponseEntity<String> {
        val command = RateRace(
            raceId = request.raceId,
            userId = request.userId,
            comment = request.comment,
            rating = request.rating
        )

        logger.info("Dispatching RateRace command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Race rating accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RateRace command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to rate race: ${ex.message}")
        }
    }
    
    /**
     * Request model for rating a race.
     */
    data class RateRaceRequest(
        val raceId: String,
        val userId: String,
        val comment: String?,
        val rating: Int
    )
}

