package io.axoniq.build.apex_racing_labs.race_search

import io.axoniq.build.apex_racing_labs.race_search.api.RaceSearchItem
import io.axoniq.build.apex_racing_labs.race_search.api.RaceSearchPerformed
import io.axoniq.build.apex_racing_labs.race_search.api.RaceSearchResult
import io.axoniq.build.apex_racing_labs.race_search.api.SearchRaces
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDate

/**
 * Race Search Command Handler - Handles commands for the Race Search Service component
 * This handler processes race search requests and publishes appropriate events
 */
class RaceSearchCommandHandler {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceSearchCommandHandler::class.java)
    }

    /**
     * Command handler for SearchRaces command
     * Processes race search requests and returns search results
     *
     * @param command The search races command containing search criteria
     * @param state The current race search state
     * @param eventAppender Event appender for publishing events
     * @return RaceSearchResult containing the search results
     */
    @CommandHandler
    fun handle(
        command: SearchRaces,
        @InjectEntity state: RaceSearchState,
        eventAppender: EventAppender
    ): RaceSearchResult {
        logger.info("Processing SearchRaces command with search term: ${command.searchTerm}")

        // Simulate race search logic - in a real implementation this would query a database
        val searchResults = performRaceSearch(command.searchTerm)

        // Publish RaceSearchPerformed event
        val event = RaceSearchPerformed(
            searchTerm = command.searchTerm,
            resultsCount = searchResults.size,
            userId = command.userId
        )
        eventAppender.append(event)

        logger.info("Race search completed with ${searchResults.size} results for term: ${command.searchTerm}")

        return RaceSearchResult(
            success = true,
            races = searchResults
        )
    }

    /**
     * Simulates race search functionality
     * In a real implementation, this would query a database or external service
     */
    private fun performRaceSearch(searchTerm: String): List<RaceSearchItem> {
        // Mock search results based on search term
        return when {
            searchTerm.lowercase().contains("monaco") -> listOf(
                RaceSearchItem("race-001", LocalDate.of(2024, 5, 26), "Monaco Street Circuit"),
                RaceSearchItem("race-002", LocalDate.of(2023, 5, 28), "Monaco Street Circuit")
            )
            searchTerm.lowercase().contains("silverstone") -> listOf(
                RaceSearchItem("race-003", LocalDate.of(2024, 7, 7), "Silverstone Circuit")
            )
            searchTerm.lowercase().contains("spa") -> listOf(
                RaceSearchItem("race-004", LocalDate.of(2024, 7, 28), "Spa-Francorchamps")
            )
            else -> emptyList()
        }
    }
}

