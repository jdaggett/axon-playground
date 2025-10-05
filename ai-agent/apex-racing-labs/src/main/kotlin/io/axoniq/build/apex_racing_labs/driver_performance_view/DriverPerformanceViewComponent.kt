package io.axoniq.build.apex_racing_labs.driver_performance_view

import io.axoniq.build.apex_racing_labs.driver_performance_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * Query component for handling driver performance data.
 * This component is responsible for maintaining a read model of driver performances
 * and handling queries related to driver race performance.
 */
@Component
class DriverPerformanceViewComponent(
    private val driverPerformanceRepository: DriverPerformanceRepository,
    private val userDriverRatingRepository: UserDriverRatingRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverPerformanceViewComponent::class.java)
    }

    /**
     * Handles queries for driver race performance data.
     * Returns comprehensive performance information including ratings and averages.
     */
    @QueryHandler
    fun handle(query: DriverRacePerformance): DriverRacePerformanceResult {
        logger.info("Handling DriverRacePerformance query for driverId: ${query.driverId}, raceId: ${query.raceId}")

        val performance = driverPerformanceRepository.findByDriverIdAndRaceId(query.driverId, query.raceId)
            ?: return DriverRacePerformanceResult(
                totalRatings = 0,
                communityRating = null,
                driverId = query.driverId,
                personalRating = null,
                raceId = query.raceId,
                averageRating = null,
                driverName = "Unknown Driver"
            )

        return DriverRacePerformanceResult(
            totalRatings = performance.totalRatings,
            communityRating = performance.averageRating?.toDouble(),
            driverId = performance.driverId,
            personalRating = null, // Personal rating would require user context
            raceId = performance.raceId,
            averageRating = performance.averageRating?.toDouble(),
            driverName = performance.driverName
        )
    }

    /**
     * Handles driver performance rated events.
     * Creates or updates driver performance data when a new rating is submitted.
     */
    @EventHandler
    fun on(event: DriverPerformanceRated) {
        logger.info("Processing DriverPerformanceRated event for driverId: ${event.driverId}, raceId: ${event.raceId}")

        val performanceId = "${event.driverId}-${event.raceId}"
        val existingPerformance = driverPerformanceRepository.findByDriverIdAndRaceId(event.driverId, event.raceId)
        
        if (existingPerformance == null) {
            // Create new performance record
            val newPerformance = DriverPerformanceEntity(
                id = performanceId,
                driverId = event.driverId,
                raceId = event.raceId,
                driverName = "Driver ${event.driverId}", // Default name, could be enhanced with driver name lookup
                totalRatings = 1,
                averageRating = BigDecimal.valueOf(event.rating.toLong()).setScale(2, RoundingMode.HALF_UP)
            )
            val savedPerformance = driverPerformanceRepository.save(newPerformance)

            // Create user rating record
            val userRating = UserDriverRating(
                userId = event.userId,
                rating = event.rating,
                driverPerformance = savedPerformance
            )
            userDriverRatingRepository.save(userRating)

        } else {
            // Check if user already rated this performance
            val existingUserRating = userDriverRatingRepository.findByUserIdAndDriverPerformance(event.userId, existingPerformance)
            
            if (existingUserRating == null) {
                // Add new user rating
                val newTotalRatings = existingPerformance.totalRatings + 1
                val currentSum = existingPerformance.averageRating?.multiply(BigDecimal.valueOf(existingPerformance.totalRatings.toLong())) ?: BigDecimal.ZERO
                val newSum = currentSum.add(BigDecimal.valueOf(event.rating.toLong()))
                val newAverage = newSum.divide(BigDecimal.valueOf(newTotalRatings.toLong()), 2, RoundingMode.HALF_UP)

                val updatedPerformance = existingPerformance.copy(
                    totalRatings = newTotalRatings,
                    averageRating = newAverage
                )
                val savedPerformance = driverPerformanceRepository.save(updatedPerformance)

                // Create user rating record
                val userRating = UserDriverRating(
                    userId = event.userId,
                    rating = event.rating,
                    driverPerformance = savedPerformance
                )
                userDriverRatingRepository.save(userRating)
            }
        }
    }

    /**
     * Handles driver rating updated events.
     * Updates existing driver performance data when a rating is modified.
     */
    @EventHandler
    fun on(event: DriverRatingUpdated) {
        logger.info("Processing DriverRatingUpdated event for driverId: ${event.driverId}, raceId: ${event.raceId}")
        
        val existingPerformance = driverPerformanceRepository.findByDriverIdAndRaceId(event.driverId, event.raceId)
        if (existingPerformance != null) {
            val existingUserRating = userDriverRatingRepository.findByUserIdAndDriverPerformance(event.userId, existingPerformance)

            if (existingUserRating != null) {
                // Update user rating
                val updatedUserRating = existingUserRating.copy(rating = event.newRating)
                userDriverRatingRepository.save(updatedUserRating)

                // Recalculate average rating
                val currentSum = existingPerformance.averageRating?.multiply(BigDecimal.valueOf(existingPerformance.totalRatings.toLong())) ?: BigDecimal.ZERO
                val adjustedSum = currentSum.subtract(BigDecimal.valueOf(event.previousRating.toLong())).add(BigDecimal.valueOf(event.newRating.toLong()))
                val newAverage = adjustedSum.divide(BigDecimal.valueOf(existingPerformance.totalRatings.toLong()), 2, RoundingMode.HALF_UP)

                val updatedPerformance = existingPerformance.copy(averageRating = newAverage)
                driverPerformanceRepository.save(updatedPerformance)
            }
        }
    }
}

