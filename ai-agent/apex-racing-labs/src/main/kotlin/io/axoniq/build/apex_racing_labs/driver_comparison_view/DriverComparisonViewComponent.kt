package io.axoniq.build.apex_racing_labs.driver_comparison_view

import io.axoniq.build.apex_racing_labs.driver_comparison_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Driver Comparison View component that provides head-to-head driver comparison analytics.
 * This component handles events to build a read model for driver comparisons and responds to queries
 * for driver profiles and comparison charts.
 */
@Component
class DriverComparisonViewComponent(
    private val driverRepository: DriverComparisonRepository,
    private val raceComparisonRepository: RaceComparisonRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverComparisonViewComponent::class.java)
    }

    /**
     * Query handler for retrieving driver profile information.
     * Returns driver details including overall rating and team information.
     */
    @QueryHandler
    fun handle(query: DriverProfile): DriverProfileResult {
        logger.info("Handling DriverProfile query for driver: {}", query.driverId)

        val driver = driverRepository.findByDriverId(query.driverId)
            ?: throw IllegalArgumentException("Driver not found: ${query.driverId}")

        // Calculate overall rating if not already set
        val overallRating = driver.overallRating ?: calculateOverallRating(driver)

        return DriverProfileResult(
            teamId = driver.teamId,
            driverId = driver.driverId,
            overallRating = overallRating,
            teamName = getTeamName(driver.teamId),
            driverName = driver.driverName
        )
    }

    /**
     * Query handler for retrieving driver comparison charts data.
     * Returns head-to-head race comparisons between two drivers.
     */
    @QueryHandler
    fun handle(query: DriverComparisonCharts): DriverComparisonResult {
        logger.info("Handling DriverComparisonCharts query for drivers: {} vs {}", 
                   query.driverId, query.rivalDriverId)

        val driver = driverRepository.findByDriverId(query.driverId)
            ?: throw IllegalArgumentException("Driver not found: ${query.driverId}")

        val rivalDriver = driverRepository.findByDriverId(query.rivalDriverId)
            ?: throw IllegalArgumentException("Rival driver not found: ${query.rivalDriverId}")

        // Get head-to-head race comparisons
        val driverRaces = raceComparisonRepository.findHeadToHeadComparisons(query.driverId, query.rivalDriverId)
        val rivalRaces = raceComparisonRepository.findHeadToHeadComparisons(query.rivalDriverId, query.driverId)

        val headToHeadRaces = mergeRaceComparisons(driverRaces, rivalRaces)

        return DriverComparisonResult(
            driverId = driver.driverId,
            rivalDriverName = rivalDriver.driverName,
            rivalDriverId = rivalDriver.driverId,
            headToHeadRaces = headToHeadRaces,
            driverName = driver.driverName
        )
    }

    /**
     * Event handler for DriverCreated events.
     * Creates a new driver entry in the view when a driver is created.
     */
    @EventHandler
    fun on(event: DriverCreated) {
        logger.info("Handling DriverCreated event for driver: {} - {}", event.driverId, event.driverName)

        val driver = DriverEntity(
            driverId = event.driverId,
            driverName = event.driverName,
            teamId = event.teamId,
            overallRating = null
        )

        driverRepository.save(driver)
        logger.info("Created driver entity for: {}", event.driverId)
    }

    /**
     * Event handler for DriverPerformanceRated events.
     * Updates driver performance ratings in race comparisons.
     */
    @EventHandler
    fun on(event: DriverPerformanceRated) {
        logger.info("Handling DriverPerformanceRated event for driver: {} in race: {}", 
                   event.driverId, event.raceId)

        val driver = driverRepository.findByDriverId(event.driverId)
            ?: throw IllegalArgumentException("Driver not found: ${event.driverId}")

        // Find or create race comparison entry
        var raceComparison = raceComparisonRepository.findByRaceIdAndDriverDriverId(event.raceId, event.driverId)

        if (raceComparison == null) {
            raceComparison = RaceComparisonEntity(
                raceId = event.raceId,
                raceDate = LocalDate.now(), // This should ideally come from race data
                trackName = "Unknown Track", // This should ideally come from race data
                driverRating = event.rating.toDouble(),
                driver = driver
            )
        } else {
            raceComparison = raceComparison.copy(driverRating = event.rating.toDouble())
        }

        raceComparisonRepository.save(raceComparison)

        // Update overall rating
        updateOverallRating(driver)

        logger.info("Updated performance rating for driver: {} in race: {}", event.driverId, event.raceId)
    }

    /**
     * Event handler for DriverRatingUpdated events.
     * Updates existing driver ratings in race comparisons.
     */
    @EventHandler
    fun on(event: DriverRatingUpdated) {
        logger.info("Handling DriverRatingUpdated event for driver: {} in race: {}", 
                   event.driverId, event.raceId)

        val raceComparison = raceComparisonRepository.findByRaceIdAndDriverDriverId(event.raceId, event.driverId)
            ?: throw IllegalArgumentException("Race comparison not found for driver: ${event.driverId} in race: ${event.raceId}")

        val updatedComparison = raceComparison.copy(driverRating = event.newRating.toDouble())
        raceComparisonRepository.save(updatedComparison)

        val driver = driverRepository.findByDriverId(event.driverId)!!
        updateOverallRating(driver)

        logger.info("Updated rating from {} to {} for driver: {} in race: {}", 
                   event.previousRating, event.newRating, event.driverId, event.raceId)
    }

    /**
     * Calculates overall rating for a driver based on their race performances.
     */
    private fun calculateOverallRating(driver: DriverEntity): Double? {
        val raceRatings = raceComparisonRepository.findByDriverDriverId(driver.driverId)
            .mapNotNull { it.driverRating }

        return if (raceRatings.isNotEmpty()) {
            raceRatings.average()
        } else {
            null
        }
    }

    /**
     * Updates the overall rating for a driver and saves it to the database.
     */
    private fun updateOverallRating(driver: DriverEntity) {
        val newOverallRating = calculateOverallRating(driver)
        val updatedDriver = driver.copy(overallRating = newOverallRating)
        driverRepository.save(updatedDriver)
    }

    /**
     * Retrieves team name for a given team ID.
     * This is a placeholder implementation that should be replaced with actual team data lookup.
     */
    private fun getTeamName(teamId: String): String {
        // This should ideally query a team service or repository
        return "Team $teamId"
    }

    /**
     * Merges race comparison data from both drivers to create head-to-head comparisons.
     */
    private fun mergeRaceComparisons(driverRaces: List<RaceComparisonEntity>, 
                                   rivalRaces: List<RaceComparisonEntity>): List<RaceComparison> {
        val raceMap = mutableMapOf<String, RaceComparison>()

        // Add driver's race data
        driverRaces.forEach { race ->
            raceMap[race.raceId] = RaceComparison(
                rivalRating = null,
                driverRating = race.driverRating,
                raceId = race.raceId,
                raceDate = race.raceDate,
                trackName = race.trackName
            )
        }

        // Add rival's race data
        rivalRaces.forEach { race ->
            val existing = raceMap[race.raceId]
            if (existing != null) {
                raceMap[race.raceId] = existing.copy(rivalRating = race.driverRating)
            } else {
                raceMap[race.raceId] = RaceComparison(
                    rivalRating = race.driverRating,
                    driverRating = null,
                    raceId = race.raceId,
                    raceDate = race.raceDate,
                    trackName = race.trackName
                )
            }
        }

        return raceMap.values.sortedByDescending { it.raceDate }
    }
}

