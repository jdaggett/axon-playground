package io.axoniq.build.apex_racing_labs.race_search

import io.axoniq.build.apex_racing_labs.race_search.api.RaceSearchPerformed
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator

/**
 * Race Search State - Event sourced entity for managing race search state
 * This entity maintains the current state of race searches based on historical events
 */
@EventSourcedEntity
class RaceSearchState {
    private var searchTerm: String = ""
    private var searchResults: MutableList<String> = mutableListOf()

    /**
     * Gets the current search term
     */
    fun getSearchTerm(): String = searchTerm

    /**
     * Gets the current search results
     */
    fun getSearchResults(): List<String> = searchResults.toList()

    /**
     * Default constructor for entity creation
     */
    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for RaceSearchPerformed event
     * Updates the search state when a race search is performed
     */
    @EventSourcingHandler
    fun evolve(event: RaceSearchPerformed) {
        this.searchTerm = event.searchTerm
        // Store the results count as a string for simplicity
        this.searchResults.add("Search performed: ${event.resultsCount} results found")
    }
}

