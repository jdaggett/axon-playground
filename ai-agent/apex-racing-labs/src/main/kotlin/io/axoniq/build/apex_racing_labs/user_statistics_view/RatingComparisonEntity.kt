package io.axoniq.build.apex_racing_labs.user_statistics_view

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * JPA entity representing individual rating comparisons for the User Statistics View component.
 * This entity stores race-specific rating data comparing personal ratings with community averages.
 */
@Entity
@Table(name = "rating_comparisons")
data class RatingComparisonEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id")
    val userId: String,

    @Column(name = "race_id")
    val raceId: String,
    
    @Column(name = "personal_rating")
    val personalRating: Int,

    @Column(name = "community_rating", precision = 10, scale = 2)
    val communityRating: BigDecimal,

    @Column(name = "difference", precision = 10, scale = 2)
    val difference: BigDecimal,

    @Column(name = "track_name")
    val trackName: String
) {
    // JPA requires a no-argument constructor
    constructor() : this(null, "", "", 0, BigDecimal.ZERO, BigDecimal.ZERO, "")
}

