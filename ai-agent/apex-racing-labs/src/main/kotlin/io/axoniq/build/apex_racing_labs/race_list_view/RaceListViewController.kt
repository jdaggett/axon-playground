package io.axoniq.build.apex_racing_labs.race_list_view

import io.axoniq.build.apex_racing_labs.race_list_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Race List View component.
 * Provides HTTP endpoints for querying race listings and searching races.
 */
@RestController
@RequestMapping("/api/races")
class RaceListViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceListViewController::class.java)
    }

    /**
     * GET endpoint to retrieve all races in chronological order.
     * Returns a list of races with their ratings and status information.
     */
    @GetMapping
    fun getRaces(): CompletableFuture<RaceListResult> {
        logger.info("Received request for race list")
        val query = RaceList()
        return queryGateway.query(query, RaceListResult::class.java, null)
    }

    /**
     * GET endpoint to search races by track name.
     * Returns races that match the search term in their track name.
     */
    @GetMapping("/search")
    fun searchRaces(@RequestParam searchTerm: String): CompletableFuture<RaceSearchResult> {
        logger.info("Received search request for term: '{}'", searchTerm)
        val query = RaceSearchResults(searchTerm = searchTerm)
        return queryGateway.query(query, RaceSearchResult::class.java, null)
    }
}