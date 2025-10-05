package io.axoniq.build.apex_racing_labs.user_statistics_view

import io.axoniq.build.apex_racing_labs.user_statistics_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.math.RoundingMode

/**
 * User Statistics View component for the Apex Racing Labs system.
 * This component handles events related to race ratings and provides query capabilities
 * for personal vs community rating comparisons.
 */
@Component
class UserStatisticsViewComponent(
    private val userStatisticsRepository: UserStatisticsRepository,
    private val ratingComparisonRepository: RatingComparisonRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(UserStatisticsViewComponent::class.java)
    }

    /**
     * Handles queries for personal vs community statistics.
     * Returns comprehensive user statistics including rating comparisons.
     * @param query the PersonalVsCommunityStatistics query
     * @return UserStatisticsResult containing all user rating data
     */
    @QueryHandler
    fun handle(query: PersonalVsCommunityStatistics): UserStatisticsResult {
        logger.info("Processing PersonalVsCommunityStatistics query for user: ${query.userId}")
        
        val userStats = userStatisticsRepository.findByUserIdWithComparisons(query.userId)
        
        return if (userStats != null) {
            val ratingComparisons = ratingComparisonRepository.findByUserId(query.userId).map { entity ->
                RatingComparison(
                    raceId = entity.raceId,
                    personalRating = entity.personalRating,
                    communityRating = entity.communityRating.toDouble(),
                    difference = entity.difference.toDouble(),
                    trackName = entity.trackName
                )
            }

            UserStatisticsResult(
                userId = userStats.userId,
                personalAverageRating = userStats.personalAverageRating?.toDouble(),
                communityAverageRating = userStats.communityAverageRating?.toDouble(),
                totalRatingsGiven = userStats.totalRatingsGiven,
                ratingDifferences = ratingComparisons
            )
        } else {
            // Return empty statistics for non-existent user
            UserStatisticsResult(
                userId = query.userId,
                personalAverageRating = null,
                communityAverageRating = null,
                totalRatingsGiven = 0,
                ratingDifferences = emptyList()
            )
        }
    }
    
    /**
     * Handles RaceRated events to update user statistics.
     * Updates personal ratings and recalculates averages when a user rates a race.
     * @param event the RaceRated event
     */
    @EventHandler
    @Transactional
    fun on(event: RaceRated) {
        logger.info("Processing RaceRated event for user: ${event.userId}, race: ${event.raceId}")

        // Find or create user statistics
        val userStats = userStatisticsRepository.findById(event.userId).orElse(
            UserStatisticsEntity(
                userId = event.userId,
                totalRatingsGiven = 0
            )
        )
        
        // Update total ratings count
        val updatedStats = userStats.copy(
            totalRatingsGiven = userStats.totalRatingsGiven + 1
        )

        // Recalculate personal average rating
        val allUserRatings = ratingComparisonRepository.findByUserId(event.userId)
        val allRatings = allUserRatings.map { it.personalRating } + event.rating
        val newPersonalAverage = if (allRatings.isNotEmpty()) {
            BigDecimal(allRatings.average()).setScale(2, RoundingMode.HALF_UP)
        } else null

        val finalStats = updatedStats.copy(personalAverageRating = newPersonalAverage)
        userStatisticsRepository.save(finalStats)

        logger.debug("Updated user statistics for user: ${event.userId}")
    }

    /**
     * Handles DriverPerformanceRated events to update rating comparisons.
     * Creates or updates rating comparison data when a driver performance is rated.
     * @param event the DriverPerformanceRated event
     */
    @EventHandler
    @Transactional
    fun on(event: DriverPerformanceRated) {
        logger.info("Processing DriverPerformanceRated event for user: ${event.userId}, race: ${event.raceId}")

        // Find existing comparison or create new one
        val existingComparison = ratingComparisonRepository.findByUserIdAndRaceId(event.userId, event.raceId)

        if (existingComparison != null) {
            // Update existing comparison with new personal rating
            val updatedComparison = existingComparison.copy(
                personalRating = event.rating,
                difference = BigDecimal(event.rating).subtract(existingComparison.communityRating)
            )
            ratingComparisonRepository.save(updatedComparison)
        } else {
            // Create new comparison with placeholder community data
            val newComparison = RatingComparisonEntity(
                userId = event.userId,
                raceId = event.raceId,
                personalRating = event.rating,
                communityRating = BigDecimal.ZERO, // Will be updated when community data is available
                difference = BigDecimal(event.rating),
                trackName = "Unknown" // Will be updated when track data is available
            )
            ratingComparisonRepository.save(newComparison)
        }

        logger.debug("Updated rating comparison for user: ${event.userId}, race: ${event.raceId}")
    }

    /**
     * Handles DriverRatingUpdated events to update existing rating comparisons.
     * Updates rating comparison data when a driver rating is modified.
     * @param event the DriverRatingUpdated event
     */
    @EventHandler
    @Transactional
    fun on(event: DriverRatingUpdated) {
        logger.info("Processing DriverRatingUpdated event for user: ${event.userId}, race: ${event.raceId}")

        val existingComparison = ratingComparisonRepository.findByUserIdAndRaceId(event.userId, event.raceId)

        if (existingComparison != null) {
            // Update the personal rating with the new rating
            val updatedComparison = existingComparison.copy(
                personalRating = event.newRating,
                difference = BigDecimal(event.newRating).subtract(existingComparison.communityRating)
            )
            ratingComparisonRepository.save(updatedComparison)

            // Recalculate user's personal average rating
            updatePersonalAverageRating(event.userId)

            logger.debug("Updated rating comparison after rating update for user: ${event.userId}")
        } else {
            logger.warn("No existing rating comparison found for user: ${event.userId}, race: ${event.raceId}")
        }
    }

    /**
     * Recalculates and updates the personal average rating for a user.
     * @param userId the user ID to recalculate averages for
     */
    private fun updatePersonalAverageRating(userId: String) {
        val userStats = userStatisticsRepository.findById(userId).orElse(null)
        if (userStats != null) {
            val allComparisons = ratingComparisonRepository.findByUserId(userId)
            val newAverage = if (allComparisons.isNotEmpty()) {
                val average = allComparisons.map { it.personalRating }.average()
                BigDecimal(average).setScale(2, RoundingMode.HALF_UP)
            } else null

            val updatedStats = userStats.copy(personalAverageRating = newAverage)
            userStatisticsRepository.save(updatedStats)
        }
    }
}

