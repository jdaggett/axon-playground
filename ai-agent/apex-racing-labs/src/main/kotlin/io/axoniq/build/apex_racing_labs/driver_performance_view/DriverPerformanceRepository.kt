package io.axoniq.build.apex_racing_labs.driver_performance_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for accessing driver performance data.
 * Part of the Driver Performance View component.
 */
@Repository
interface DriverPerformanceRepository : JpaRepository<DriverPerformanceEntity, String> {
    
    /**
     * Find driver performance by driver ID and race ID.
     */
    fun findByDriverIdAndRaceId(driverId: String, raceId: String): DriverPerformanceEntity?
}

