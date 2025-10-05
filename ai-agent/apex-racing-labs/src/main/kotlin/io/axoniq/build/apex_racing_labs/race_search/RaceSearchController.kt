package io.axoniq.build.apex_racing_labs.race_search

import io.axoniq.build.apex_racing_labs.race_search.api.SearchRaces
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST Controller for Race Search Service
 * Provides HTTP endpoints for race search functionality
 */
@RestController
@RequestMapping("/api/race-search")
class RaceSearchController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceSearchController::class.java)
    }

    /**
     * Search for races endpoint
     * Accepts search requests and dispatches SearchRaces command
     */
    @PostMapping("/search")
    fun searchRaces(@RequestBody request: SearchRacesRequest): ResponseEntity<String> {
        val command = SearchRaces(
            searchTerm = request.searchTerm,
            userId = request.userId
        )
        
        logger.info("Dispatching SearchRaces command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Race search request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch SearchRaces command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to process race search request")
        }
    }
}

/**
 * Request model for race search endpoint
 */
data class SearchRacesRequest(
    val searchTerm: String,
    val userId: String?
)