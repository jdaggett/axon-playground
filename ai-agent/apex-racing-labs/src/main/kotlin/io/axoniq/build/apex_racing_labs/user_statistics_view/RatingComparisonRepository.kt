package io.axoniq.build.apex_racing_labs.user_statistics_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for RatingComparisonEntity in the User Statistics View component.
 * Provides data access methods for individual rating comparisons.
 */
@Repository
interface RatingComparisonRepository : JpaRepository<RatingComparisonEntity, Long> {

    /**
     * Finds all rating comparisons for a specific user.
     * @param userId the user ID to search for
     * @return list of rating comparison entities
     */
    fun findByUserId(userId: String): List<RatingComparisonEntity>

    /**
     * Finds a rating comparison by user ID and race ID.
     * @param userId the user ID
     * @param raceId the race ID
     * @return the rating comparison entity if found
     */
    fun findByUserIdAndRaceId(userId: String, raceId: String): RatingComparisonEntity?
}

