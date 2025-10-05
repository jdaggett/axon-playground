package io.axoniq.build.apex_racing_labs.race_profile_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing a race profile in the Race Profile View component.
 * This entity stores race information including ratings, participating drivers, and user comments.
 */
@Entity
@Table(name = "race_profiles")
data class RaceProfileEntity(
    @Id
    val raceId: String = "",

    val trackName: String = "",

    val raceDate: LocalDate = LocalDate.now(),

    val status: String = "",

    val totalRatings: Int = 0,

    val averageRating: Double? = null,

    @OneToMany(mappedBy = "raceId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val userComments: MutableList<UserCommentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "raceId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val participatingDrivers: MutableList<DriverInfoEntity> = mutableListOf()
)

/**
 * JPA entity representing user comments and ratings for races.
 * Part of the Race Profile View component model.
 */
@Entity
@Table(name = "user_comments")
data class UserCommentEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val raceId: String = "",

    val userId: String = "",

    val comment: String? = null,

    val rating: Int = 0
)

/**
 * JPA entity representing participating driver information for races.
 * Part of the Race Profile View component model.
 */
@Entity
@Table(name = "driver_info")
data class DriverInfoEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val raceId: String = "",
    
    val driverId: String = "",

    val driverName: String = "",

    val averageRating: Double? = null
)

