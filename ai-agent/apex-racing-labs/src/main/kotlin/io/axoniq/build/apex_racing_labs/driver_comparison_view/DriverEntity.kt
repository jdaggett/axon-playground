package io.axoniq.build.apex_racing_labs.driver_comparison_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing a driver for the Driver Comparison View component.
 * This entity stores driver information and their race performance ratings.
 */
@Entity
@Table(name = "driver_comparison")
data class DriverEntity(
    @Id
    val driverId: String,
    
    val driverName: String,
    
    val teamId: String,
    
    val overallRating: Double? = null,

    @OneToMany(mappedBy = "driver", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val raceComparisons: MutableList<RaceComparisonEntity> = mutableListOf()
) {
    constructor() : this("", "", "", null, mutableListOf())
}

/**
 * JPA entity representing race comparison data for drivers.
 * This entity stores individual race performance ratings for driver comparisons.
 */
@Entity
@Table(name = "race_comparison")
data class RaceComparisonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    val raceId: String,
    
    val raceDate: LocalDate,

    val trackName: String,

    val driverRating: Double? = null,

    val rivalRating: Double? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    val driver: DriverEntity
) {
    constructor() : this(null, "", LocalDate.now(), "", null, null, DriverEntity())
}

