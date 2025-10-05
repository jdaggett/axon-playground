package io.axoniq.build.apex_racing_labs.team_performance_view

import io.axoniq.build.apex_racing_labs.team_performance_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

/**
 * Team Performance Statistics View component that handles team performance analytics and statistics.
 * This component listens to racing-related events and maintains aggregated performance data
 * for teams, including race participation, ratings, and best performances.
 */
@Component
class TeamPerformanceViewComponent(
    private val teamPerformanceRepository: TeamPerformanceRepository,
    private val teamRaceInfoRepository: TeamRaceInfoRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(TeamPerformanceViewComponent::class.java)
    }

    /**
     * Handles TeamPerformanceStatistics query for the Team Performance Statistics View component.
     * Returns comprehensive performance statistics for a specific team.
     */
    @QueryHandler
    fun handle(query: TeamPerformanceStatistics): TeamPerformanceResult {
        logger.info("Processing TeamPerformanceStatistics query for team: ${query.teamId}")
        
        val teamPerformance = teamPerformanceRepository.findById(query.teamId)
            .orElseThrow { IllegalArgumentException("Team not found: ${query.teamId}") }

        val bestRaces = teamRaceInfoRepository.findByTeamPerformanceTeamId(query.teamId)
            .map { raceInfo ->
                TeamRaceInfo(
                    raceId = raceInfo.raceId,
                    raceDate = raceInfo.raceDate,
                    trackName = raceInfo.trackName,
                    averageRating = raceInfo.averageRating
                )
            }

        return TeamPerformanceResult(
            teamId = teamPerformance.teamId,
            teamName = teamPerformance.teamName,
            totalRaces = teamPerformance.totalRaces,
            averageRating = teamPerformance.averageRating,
            bestRaces = bestRaces
        )
    }

    /**
     * Handles RaceCreated event for the Team Performance Statistics View component.
     * Updates team performance statistics when a new race is created.
     */
    @EventHandler
    @Transactional
    fun on(event: RaceCreated) {
        logger.info("Processing RaceCreated event for race: ${event.raceId}")

        // For each participating driver, we need to determine their team and update team statistics
        event.participatingDriverIds.forEach { driverId ->
            // In a real implementation, we would need to look up the driver's team
            // For now, we'll create a placeholder team name based on driver ID
            val teamId = "team-${driverId.take(3)}"
            val teamName = "Team ${driverId.take(3).uppercase()}"

            val teamPerformance = teamPerformanceRepository.findById(teamId)
                .orElse(TeamPerformanceEntity(
                    teamId = teamId,
                    teamName = teamName,
                    totalRaces = 0,
                    averageRating = null
                ))

            val updatedTeamPerformance = teamPerformance.copy(
                totalRaces = teamPerformance.totalRaces + 1
            )

            teamPerformanceRepository.save(updatedTeamPerformance)

            // Create race info entry
            val raceInfo = TeamRaceInfoEntity(
                raceId = event.raceId,
                raceDate = event.raceDate,
                trackName = event.trackName,
                averageRating = 0.0, // Will be updated when ratings come in
                teamPerformance = updatedTeamPerformance
            )

            teamRaceInfoRepository.save(raceInfo)
        }
    }

    /**
     * Handles RaceRated event for the Team Performance Statistics View component.
     * Updates team performance statistics when a race receives a rating.
     */
    @EventHandler
    @Transactional
    fun on(event: RaceRated) {
        logger.info("Processing RaceRated event for race: ${event.raceId}")

        // Find all race info entities for this race and update their ratings
        val raceInfoEntities = teamRaceInfoRepository.findByRaceId(event.raceId)
        
        raceInfoEntities.forEach { raceInfo ->
            val updatedRaceInfo = raceInfo.copy(
                averageRating = event.rating.toDouble()
            )
            teamRaceInfoRepository.save(updatedRaceInfo)

            // Update team's overall average rating
            updateTeamAverageRating(raceInfo.teamPerformance?.teamId ?: "")
        }
    }

    /**
     * Handles DriverPerformanceRated event for the Team Performance Statistics View component.
     * Updates team performance statistics when a driver's performance is rated.
     */
    @EventHandler
    @Transactional
    fun on(event: DriverPerformanceRated) {
        logger.info("Processing DriverPerformanceRated event for driver: ${event.driverId} in race: ${event.raceId}")

        // Determine driver's team and update race performance
        val teamId = "team-${event.driverId.take(3)}"
        updateRacePerformance(event.raceId, teamId, event.rating.toDouble())
    }

    /**
     * Handles DriverRatingUpdated event for the Team Performance Statistics View component.
     * Updates team performance statistics when a driver's rating is updated.
     */
    @EventHandler
    @Transactional
    fun on(event: DriverRatingUpdated) {
        logger.info("Processing DriverRatingUpdated event for driver: ${event.driverId} in race: ${event.raceId}")

        // Determine driver's team and update race performance
        val teamId = "team-${event.driverId.take(3)}"
        updateRacePerformance(event.raceId, teamId, event.newRating.toDouble())
    }

    /**
     * Updates the race performance for a specific team and race.
     */
    private fun updateRacePerformance(raceId: String, teamId: String, rating: Double) {
        val raceInfoEntities = teamRaceInfoRepository.findByRaceId(raceId)

        raceInfoEntities.forEach { raceInfo ->
            if (raceInfo.teamPerformance?.teamId == teamId) {
                val updatedRaceInfo = raceInfo.copy(averageRating = rating)
                teamRaceInfoRepository.save(updatedRaceInfo)
                updateTeamAverageRating(teamId)
            }
        }
    }

    /**
     * Recalculates and updates the team's overall average rating.
     */
    private fun updateTeamAverageRating(teamId: String) {
        if (teamId.isBlank()) return

        val teamPerformance = teamPerformanceRepository.findById(teamId).orElse(null) ?: return
        val raceInfos = teamRaceInfoRepository.findByTeamPerformanceTeamId(teamId)

        val averageRating = if (raceInfos.isNotEmpty()) {
            raceInfos.map { it.averageRating }.average()
        } else {
            null
        }

        val updatedTeamPerformance = teamPerformance.copy(averageRating = averageRating)
        teamPerformanceRepository.save(updatedTeamPerformance)
    }
}

