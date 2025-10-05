package io.axoniq.build.apex_racing_labs.driver_performance_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for accessing user driver rating data.
 * Part of the Driver Performance View component.
 */
@Repository
interface UserDriverRatingRepository : JpaRepository<UserDriverRating, Long> {

    /**
     * Find user rating by user ID and driver performance entity.
     */
    fun findByUserIdAndDriverPerformance(userId: String, driverPerformance: DriverPerformanceEntity): UserDriverRating?
}

