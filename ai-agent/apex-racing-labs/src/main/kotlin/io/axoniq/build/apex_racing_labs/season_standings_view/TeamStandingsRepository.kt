package io.axoniq.build.apex_racing_labs.season_standings_view

import org.springframework.data.jpa.repository.JpaRepository

/**
 * Repository interface for accessing TeamStandingsEntity data.
 * Used by the Season Standings View component to manage team standings data.
 */
interface TeamStandingsRepository : JpaRepository<TeamStandingsEntity, String> {

    /**
     * Finds all team standings ordered by position for displaying season rankings.
     */
    fun findAllByOrderByPosition(): List<TeamStandingsEntity>

    /**
     * Finds team standings by season for filtering seasonal data.
     */
    fun findBySeasonOrderByPosition(season: String): List<TeamStandingsEntity>
}

