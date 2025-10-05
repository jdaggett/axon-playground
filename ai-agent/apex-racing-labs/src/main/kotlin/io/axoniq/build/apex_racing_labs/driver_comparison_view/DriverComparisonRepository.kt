package io.axoniq.build.apex_racing_labs.driver_comparison_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for Driver Comparison View component.
 * Provides data access methods for driver comparison analytics.
 */
@Repository
interface DriverComparisonRepository : JpaRepository<DriverEntity, String> {

    /**
     * Finds a driver by their driver ID.
     */
    fun findByDriverId(driverId: String): DriverEntity?

    /**
     * Finds race comparisons between two drivers.
     */
    @Query("SELECT rc FROM RaceComparisonEntity rc WHERE rc.driver.driverId IN (:driverId, :rivalDriverId)")
    fun findRaceComparisonsForDrivers(@Param("driverId") driverId: String, 
                                     @Param("rivalDriverId") rivalDriverId: String): List<RaceComparisonEntity>
}

