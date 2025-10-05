package io.axoniq.build.apex_racing_labs.race_list_view

import io.axoniq.build.apex_racing_labs.race_list_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Race List View component that handles race listing queries and maintains race data.
 * Provides chronological race listings with ratings and search functionality.
 */
@Component
class RaceListViewComponent(
    private val raceRepository: RaceRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceListViewComponent::class.java)
    }

    /**
     * Handles RaceList query to return all races in chronological order.
     * Returns races with their ratings and status information.
     */
    @QueryHandler
    fun handle(query: RaceList): RaceListResult {
        logger.info("Processing RaceList query for Race List View component")

        val races = raceRepository.findAllOrderByRaceDateDesc()
        val raceItems = races.map { race ->
            RaceListItem(
                totalRatings = race.totalRatings,
                raceId = race.raceId,
                averageRating = race.averageRating,
                status = race.status,
                raceDate = race.raceDate,
                trackName = race.trackName
            )
        }

        logger.debug("Returning {} races for RaceList query", raceItems.size)
        return RaceListResult(races = raceItems)
    }

    /**
     * Handles RaceSearchResults query to search races by track name.
     * Returns races that match the search term in their track name.
     */
    @QueryHandler
    fun handle(query: RaceSearchResults): RaceSearchResult {
        logger.info("Processing RaceSearchResults query for search term: '{}'", query.searchTerm)

        val matchingRaces = raceRepository.findByTrackNameContainingIgnoreCase(query.searchTerm)
        val searchItems = matchingRaces.map { race ->
            RaceSearchItem(
                raceId = race.raceId,
                raceDate = race.raceDate,
                trackName = race.trackName
            )
        }

        logger.debug("Found {} races matching search term '{}'", searchItems.size, query.searchTerm)
        return RaceSearchResult(
            success = true,
            races = searchItems
        )
    }

    /**
     * Handles RaceCreated event to add a new race to the view.
     * Creates a new race entry with initial status and no ratings.
     */
    @EventHandler
    fun on(event: RaceCreated) {
        logger.info("Processing RaceCreated event for race: {}", event.raceId)

        val raceEntity = RaceEntity(
            raceId = event.raceId,
            raceDate = event.raceDate,
            trackName = event.trackName,
            status = "SCHEDULED",
            totalRatings = 0,
            averageRating = null
        )
        
        raceRepository.save(raceEntity)
        logger.debug("Created new race entry for race: {}", event.raceId)
    }

    /**
     * Handles RaceCancelled event to update race status.
     * Updates the race status to cancelled in the view.
     */
    @EventHandler
    fun on(event: RaceCancelled) {
        logger.info("Processing RaceCancelled event for race: {}", event.raceId)

        val existingRace = raceRepository.findById(event.raceId).orElse(null)
        if (existingRace != null) {
            val updatedRace = existingRace.copy(status = "CANCELLED")
            raceRepository.save(updatedRace)
            logger.debug("Updated race status to CANCELLED for race: {}", event.raceId)
        } else {
            logger.warn("Race not found for cancellation: {}", event.raceId)
        }
    }

    /**
     * Handles RaceRated event to update race rating information.
     * Recalculates total ratings and average rating when a new rating is added.
     */
    @EventHandler
    fun on(event: RaceRated) {
        logger.info("Processing RaceRated event for race: {} with rating: {}", event.raceId, event.rating)

        val existingRace = raceRepository.findById(event.raceId).orElse(null)
        if (existingRace != null) {
            val newTotalRatings = existingRace.totalRatings + 1
            val currentSum = (existingRace.averageRating ?: 0.0) * existingRace.totalRatings
            val newSum = currentSum + event.rating
            val newAverageRating = newSum / newTotalRatings
            
            val updatedRace = existingRace.copy(
                totalRatings = newTotalRatings,
                averageRating = newAverageRating
            )
            
            raceRepository.save(updatedRace)
            logger.debug("Updated race ratings for race: {} - Total: {}, Average: {}", 
                event.raceId, newTotalRatings, newAverageRating)
        } else {
            logger.warn("Race not found for rating update: {}", event.raceId)
        }
    }
}

