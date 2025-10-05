package io.axoniq.build.apex_racing_labs.driver_performance_view

import jakarta.persistence.*

/**
 * JPA entity representing a user's rating for a driver's performance in a specific race.
 * Part of the Driver Performance View component.
 */
@Entity
@Table(name = "user_driver_ratings")
data class UserDriverRating(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "user_id", nullable = false)
    val userId: String,

    @Column(name = "rating", nullable = false)
    val rating: Int,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_performance_id")
    val driverPerformance: DriverPerformanceEntity? = null
) {
    constructor() : this(0, "", 0, null)
}

