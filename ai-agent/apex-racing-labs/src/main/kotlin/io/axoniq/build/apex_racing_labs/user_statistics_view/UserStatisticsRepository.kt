package io.axoniq.build.apex_racing_labs.user_statistics_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for UserStatisticsEntity in the User Statistics View component.
 * Provides data access methods for user statistics and rating comparisons.
 */
@Repository
interface UserStatisticsRepository : JpaRepository<UserStatisticsEntity, String> {

    /**
     * Finds user statistics by user ID with all related rating comparisons.
     * @param userId the user ID to search for
     * @return the user statistics entity if found
     */
    @Query("SELECT u FROM UserStatisticsEntity u LEFT JOIN FETCH u.ratingComparisons WHERE u.userId = :userId")
    fun findByUserIdWithComparisons(@Param("userId") userId: String): UserStatisticsEntity?
}

