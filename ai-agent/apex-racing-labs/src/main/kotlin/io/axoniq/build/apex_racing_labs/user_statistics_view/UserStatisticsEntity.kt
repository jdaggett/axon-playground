package io.axoniq.build.apex_racing_labs.user_statistics_view

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * JPA entity representing user statistics for the User Statistics View component.
 * This entity stores aggregated data about user ratings and comparisons with community averages.
 */
@Entity
@Table(name = "user_statistics")
data class UserStatisticsEntity(
    @Id
    val userId: String,
    
    @Column(name = "personal_average_rating", precision = 10, scale = 2)
    val personalAverageRating: BigDecimal? = null,

    @Column(name = "community_average_rating", precision = 10, scale = 2)
    val communityAverageRating: BigDecimal? = null,

    @Column(name = "total_ratings_given")
    val totalRatingsGiven: Int = 0,

    @OneToMany(mappedBy = "userId", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val ratingComparisons: List<RatingComparisonEntity> = emptyList()
) {
    // JPA requires a no-argument constructor
    constructor() : this("")
}

