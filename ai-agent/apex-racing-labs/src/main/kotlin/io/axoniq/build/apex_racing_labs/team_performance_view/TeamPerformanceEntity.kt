package io.axoniq.build.apex_racing_labs.team_performance_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing team performance statistics for the Team Performance Statistics View component.
 * This entity stores aggregated performance data for racing teams including total races,
 * average ratings, and best race performances.
 */
@Entity
@Table(name = "team_performance")
data class TeamPerformanceEntity(
    @Id
    @Column(name = "team_id")
    val teamId: String,

    @Column(name = "team_name", nullable = false)
    val teamName: String,

    @Column(name = "total_races", nullable = false)
    val totalRaces: Int,

    @Column(name = "average_rating")
    val averageRating: Double?,

    @OneToMany(mappedBy = "teamPerformance", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val bestRaces: List<TeamRaceInfoEntity> = emptyList()
) {
    constructor() : this("", "", 0, null, emptyList())
}

/**
 * JPA entity representing individual race information for teams in the Team Performance Statistics View component.
 * This entity stores details about specific races that contributed to a team's performance statistics.
 */
@Entity
@Table(name = "team_race_info")
data class TeamRaceInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "race_id", nullable = false)
    val raceId: String,
    
    @Column(name = "race_date", nullable = false)
    val raceDate: LocalDate,

    @Column(name = "track_name", nullable = false)
    val trackName: String,

    @Column(name = "average_rating", nullable = false)
    val averageRating: Double,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    val teamPerformance: TeamPerformanceEntity? = null
) {
    constructor() : this(null, "", LocalDate.now(), "", 0.0, null)
}

