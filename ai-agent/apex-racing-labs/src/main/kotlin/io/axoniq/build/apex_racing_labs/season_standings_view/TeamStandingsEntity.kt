package io.axoniq.build.apex_racing_labs.season_standings_view

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * JPA Entity representing team standings for the Season Standings View component.
 * Stores aggregated data about team performance including total races, average rating,
 * and current position in the season standings.
 */
@Entity
@Table(name = "team_standings")
data class TeamStandingsEntity(
    @Id
    val teamId: String = "",

    @Column(name = "total_races", nullable = false)
    val totalRaces: Int = 0,

    @Column(name = "average_rating", precision = 10, scale = 2)
    val averageRating: BigDecimal? = null,

    @Column(name = "season", nullable = false)
    val season: String = "",

    @Column(name = "team_name", nullable = false)
    val teamName: String = "",

    @Column(name = "position", nullable = false)
    val position: Int = 0
)

