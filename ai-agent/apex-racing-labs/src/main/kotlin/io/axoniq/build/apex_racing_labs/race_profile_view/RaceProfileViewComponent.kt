package io.axoniq.build.apex_racing_labs.race_profile_view

import io.axoniq.build.apex_racing_labs.race_profile_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

/**
 * Query component for the Race Profile View.
 * Handles race profile queries and updates the read model based on racing events.
 * Provides detailed race information with ratings and participating drivers.
 */
@Component
class RaceProfileViewComponent(
    private val raceProfileRepository: RaceProfileRepository,
    private val userCommentRepository: UserCommentRepository,
    private val driverInfoRepository: DriverInfoRepository
) {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RaceProfileViewComponent::class.java)
    }

    /**
     * Handles RaceProfile queries to retrieve detailed race information.
     * Returns race profile with ratings, comments, and participating drivers.
     */
    @QueryHandler
    fun handle(query: RaceProfile): RaceProfileResult? {
        logger.info("Handling RaceProfile query for raceId: ${query.raceId}")

        val raceProfile = raceProfileRepository.findById(query.raceId).orElse(null)
            ?: return null

        val userComments = userCommentRepository.findByRaceId(query.raceId).map { 
            UserComment(
                userId = it.userId,
                comment = it.comment,
                rating = it.rating
            )
        }
        
        val participatingDrivers = driverInfoRepository.findByRaceId(query.raceId).map {
            DriverInfo(
                driverId = it.driverId,
                driverName = it.driverName,
                averageRating = it.averageRating
            )
        }

        return RaceProfileResult(
            raceId = raceProfile.raceId,
            trackName = raceProfile.trackName,
            raceDate = raceProfile.raceDate,
            status = raceProfile.status,
            totalRatings = raceProfile.totalRatings,
            averageRating = raceProfile.averageRating,
            userComments = userComments,
            participatingDrivers = participatingDrivers
        )
    }

    /**
     * Handles RaceCreated events to initialize race profile in the read model.
     */
    @EventHandler
    @Transactional
    fun on(event: RaceCreated) {
        logger.info("Handling RaceCreated event for raceId: ${event.raceId}")

        val raceProfile = RaceProfileEntity(
            raceId = event.raceId,
            trackName = event.trackName,
            raceDate = event.raceDate,
            status = "CREATED",
            totalRatings = 0,
            averageRating = null
        )

        raceProfileRepository.save(raceProfile)

        // Create driver info entries for participating drivers
        event.participatingDriverIds.forEach { driverId ->
            val driverInfo = DriverInfoEntity(
                raceId = event.raceId,
                driverId = driverId,
                driverName = "", // Will be updated when driver ratings come in
                averageRating = null
            )
            driverInfoRepository.save(driverInfo)
        }
    }

    /**
     * Handles RaceRated events to update race ratings and user comments.
     */
    @EventHandler
    @Transactional
    fun on(event: RaceRated) {
        logger.info("Handling RaceRated event for raceId: ${event.raceId}, userId: ${event.userId}")

        // Save or update user comment
        val existingComment = userCommentRepository.findByRaceIdAndUserId(event.raceId, event.userId)
        if (existingComment != null) {
            val updatedComment = existingComment.copy(
                comment = event.comment,
                rating = event.rating
            )
            userCommentRepository.save(updatedComment)
        } else {
            val newComment = UserCommentEntity(
                raceId = event.raceId,
                userId = event.userId,
                comment = event.comment,
                rating = event.rating
            )
            userCommentRepository.save(newComment)
        }

        // Update race profile with new rating statistics
        updateRaceAverageRating(event.raceId)
    }

    /**
     * Handles DriverPerformanceRated events to update driver performance information.
     */
    @EventHandler
    @Transactional
    fun on(event: DriverPerformanceRated) {
        logger.info("Handling DriverPerformanceRated event for driverId: ${event.driverId}, raceId: ${event.raceId}")

        // Update driver average rating (simplified - in real implementation would calculate from all ratings)
        val driverInfo = driverInfoRepository.findByRaceIdAndDriverId(event.raceId, event.driverId)
        if (driverInfo != null) {
            val updatedDriverInfo = driverInfo.copy(
                averageRating = event.rating.toDouble() // Simplified calculation
            )
            driverInfoRepository.save(updatedDriverInfo)
        }
    }

    /**
     * Handles DriverRatingUpdated events to update driver rating information.
     */
    @EventHandler
    @Transactional
    fun on(event: DriverRatingUpdated) {
        logger.info("Handling DriverRatingUpdated event for driverId: ${event.driverId}, raceId: ${event.raceId}")
        
        val driverInfo = driverInfoRepository.findByRaceIdAndDriverId(event.raceId, event.driverId)
        if (driverInfo != null) {
            val updatedDriverInfo = driverInfo.copy(
                averageRating = event.newRating.toDouble() // Simplified calculation
            )
            driverInfoRepository.save(updatedDriverInfo)
        }
    }

    /**
     * Handles RaceCancelled events to update race status.
     */
    @EventHandler
    @Transactional
    fun on(event: RaceCancelled) {
        logger.info("Handling RaceCancelled event for raceId: ${event.raceId}")

        val raceProfile = raceProfileRepository.findById(event.raceId).orElse(null)
        if (raceProfile != null) {
            val updatedRace = raceProfile.copy(status = "CANCELLED")
            raceProfileRepository.save(updatedRace)
        }
    }

    /**
     * Updates the average rating for a race based on all user comments.
     */
    private fun updateRaceAverageRating(raceId: String) {
        val comments = userCommentRepository.findByRaceId(raceId)
        if (comments.isNotEmpty()) {
            val averageRating = comments.map { it.rating }.average()
            val raceProfile = raceProfileRepository.findById(raceId).orElse(null)
            if (raceProfile != null) {
                val updatedRace = raceProfile.copy(
                    totalRatings = comments.size,
                    averageRating = averageRating
                )
                raceProfileRepository.save(updatedRace)
            }
        }
    }
}

