package io.axoniq.build.apex_racing_labs.race_list_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing a race in the Race List View component.
 * Stores race information for efficient querying and display in race listings.
 */
@Entity
@Table(name = "race_list_view")
data class RaceEntity(
    @Id
    @Column(name = "race_id")
    val raceId: String = "",

    @Column(name = "race_date", nullable = false)
    val raceDate: LocalDate = LocalDate.now(),

    @Column(name = "track_name", nullable = false)
    val trackName: String = "",

    @Column(name = "status", nullable = false)
    val status: String = "SCHEDULED",

    @Column(name = "total_ratings", nullable = false)
    val totalRatings: Int = 0,

    @Column(name = "average_rating")
    val averageRating: Double? = null
)

