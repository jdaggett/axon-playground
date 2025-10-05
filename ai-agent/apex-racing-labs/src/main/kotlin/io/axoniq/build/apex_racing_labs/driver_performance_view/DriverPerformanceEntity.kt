package io.axoniq.build.apex_racing_labs.driver_performance_view

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * JPA entity representing driver performance data for a specific race.
 * This is the main entity for the Driver Performance View component.
 */
@Entity
@Table(name = "driver_performances")
data class DriverPerformanceEntity(
    @Id
    @Column(name = "id")
    val id: String,

    @Column(name = "driver_id", nullable = false)
    val driverId: String,

    @Column(name = "race_id", nullable = false)
    val raceId: String,

    @Column(name = "driver_name", nullable = false)
    val driverName: String,

    @Column(name = "total_ratings", nullable = false)
    val totalRatings: Int,

    @Column(name = "average_rating", precision = 3, scale = 2)
    val averageRating: BigDecimal?,

    @OneToMany(mappedBy = "driverPerformance", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userRatings: List<UserDriverRating> = emptyList()
) {
    constructor() : this("", "", "", "", 0, null, emptyList())
}

