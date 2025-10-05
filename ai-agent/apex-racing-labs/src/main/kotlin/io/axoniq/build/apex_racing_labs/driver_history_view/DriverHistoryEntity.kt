package io.axoniq.build.apex_racing_labs.driver_history_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA Entity representing the driver performance history view model.
 * This entity stores aggregated driver performance data for the Driver Performance History View component.
 */
@Entity
@Table(name = "driver_history")
data class DriverHistoryEntity(
    @Id
    val driverId: String,
    
    val driverName: String,
    
    val totalRatings: Int = 0,

    val overallAverageRating: Double? = null,

    @OneToMany(mappedBy = "driverId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val racePerformances: MutableList<DriverRaceHistoryEntity> = mutableListOf()
) {
    constructor() : this("", "", 0, null, mutableListOf())
}

/**
 * JPA Entity representing individual race performance history for a driver.
 * This entity stores race-specific performance data for the Driver Performance History View component.
 */
@Entity
@Table(name = "driver_race_history")
data class DriverRaceHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val driverId: String,

    val raceId: String,

    val raceDate: LocalDate,

    val trackName: String,

    val averageRating: Double,

    var totalRatings: Int = 0
) {
    constructor() : this(null, "", "", LocalDate.now(), "", 0.0, 0)
}

