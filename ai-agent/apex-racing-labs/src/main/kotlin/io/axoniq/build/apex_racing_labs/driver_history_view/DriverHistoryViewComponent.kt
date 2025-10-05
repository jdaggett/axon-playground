package io.axoniq.build.apex_racing_labs.driver_history_view

import io.axoniq.build.apex_racing_labs.driver_history_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Driver Performance History View component responsible for handling driver performance queries and events.
 * This component maintains a read model of driver performance history and best races.
 */
@Component
class DriverHistoryViewComponent(
    private val driverHistoryRepository: DriverHistoryRepository,
    private val driverRaceHistoryRepository: DriverRaceHistoryRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverHistoryViewComponent::class.java)
    }

    /**
     * Handles DriverPerformanceHistory queries to retrieve driver performance history and best races.
     * Returns aggregated driver performance data including total ratings, best races, and overall average rating.
     */
    @QueryHandler
    fun handle(query: DriverPerformanceHistory): DriverHistoryResult? {
        logger.info("Handling DriverPerformanceHistory query for driver: ${query.driverId}")

        val driverHistory = driverHistoryRepository.findById(query.driverId).orElse(null)
            ?: return null

        val raceHistories = driverRaceHistoryRepository.findByDriverId(query.driverId)

        val bestRaces = raceHistories.map { raceHistory ->
            DriverRaceHistory(
                raceId = raceHistory.raceId,
                raceDate = raceHistory.raceDate,
                trackName = raceHistory.trackName,
                averageRating = raceHistory.averageRating
            )
        }

        return DriverHistoryResult(
            driverId = driverHistory.driverId,
            driverName = driverHistory.driverName,
            totalRatings = driverHistory.totalRatings,
            bestRaces = bestRaces,
            overallAverageRating = driverHistory.overallAverageRating
        )
    }

    /**
     * Handles DriverPerformanceRated events to update the driver performance history read model.
     * Creates or updates driver performance data when a new rating is submitted.
     */
    @EventHandler
    @Transactional
    fun on(event: DriverPerformanceRated) {
        logger.info("Handling DriverPerformanceRated event for driver: ${event.driverId}, race: ${event.raceId}")

        // Update or create driver history
        val driverHistory = driverHistoryRepository.findById(event.driverId).orElse(null)
            ?: DriverHistoryEntity(
                driverId = event.driverId,
                driverName = "Unknown Driver", // Will be updated when driver name is available
                totalRatings = 0,
                overallAverageRating = null
            )

        // Update or create race performance
        val raceHistory = driverRaceHistoryRepository.findByDriverIdAndRaceId(event.driverId, event.raceId)
            ?: DriverRaceHistoryEntity(
                driverId = event.driverId,
                raceId = event.raceId,
                raceDate = LocalDate.now(), // Will be updated when race date is available
                trackName = "Unknown Track", // Will be updated when track name is available
                averageRating = event.rating.toDouble(),
                totalRatings = 1
            )

        // Calculate new average for this race
        val newTotal = raceHistory.totalRatings + 1
        val newAverage = ((raceHistory.averageRating * raceHistory.totalRatings) + event.rating) / newTotal

        val updatedRaceHistory = raceHistory.copy(
            averageRating = newAverage,
            totalRatings = newTotal
        )

        driverRaceHistoryRepository.save(updatedRaceHistory)
        
        // Update overall driver statistics
        val allRaceHistories = driverRaceHistoryRepository.findByDriverId(event.driverId)
        val totalRatings = allRaceHistories.sumOf { it.totalRatings }
        val overallAverage = if (totalRatings > 0) {
            allRaceHistories.sumOf { it.averageRating * it.totalRatings } / totalRatings
        } else null

        val updatedDriverHistory = driverHistory.copy(
            totalRatings = totalRatings,
            overallAverageRating = overallAverage
        )

        driverHistoryRepository.save(updatedDriverHistory)
    }

    /**
     * Handles DriverRatingUpdated events to update the driver performance history read model.
     * Updates existing driver performance data when a rating is modified.
     */
    @EventHandler
    @Transactional
    fun on(event: DriverRatingUpdated) {
        logger.info("Handling DriverRatingUpdated event for driver: ${event.driverId}, race: ${event.raceId}")

        val raceHistory = driverRaceHistoryRepository.findByDriverIdAndRaceId(event.driverId, event.raceId)
        if (raceHistory != null) {
            // Recalculate average rating for the race
            val totalRatingSum = (raceHistory.averageRating * raceHistory.totalRatings) - event.previousRating + event.newRating
            val newAverage = totalRatingSum / raceHistory.totalRatings
            
            val updatedRaceHistory = raceHistory.copy(averageRating = newAverage)
            driverRaceHistoryRepository.save(updatedRaceHistory)

            // Update overall driver statistics
            val driverHistory = driverHistoryRepository.findById(event.driverId).orElse(null)
            if (driverHistory != null) {
                val allRaceHistories = driverRaceHistoryRepository.findByDriverId(event.driverId)
                val totalRatings = allRaceHistories.sumOf { it.totalRatings }
                val overallAverage = if (totalRatings > 0) {
                    allRaceHistories.sumOf { it.averageRating * it.totalRatings } / totalRatings
                } else null

                val updatedDriverHistory = driverHistory.copy(overallAverageRating = overallAverage)
                driverHistoryRepository.save(updatedDriverHistory)
            }
        }
    }
}

