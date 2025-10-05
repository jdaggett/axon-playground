package io.axoniq.build.apex_racing_labs.team_performance_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for team race information entities in the Team Performance Statistics View component.
 * Provides data access methods for individual race performance data.
 */
@Repository
interface TeamRaceInfoRepository : JpaRepository<TeamRaceInfoEntity, Long> {
    fun findByTeamPerformanceTeamId(teamId: String): List<TeamRaceInfoEntity>
    fun findByRaceId(raceId: String): List<TeamRaceInfoEntity>
}

