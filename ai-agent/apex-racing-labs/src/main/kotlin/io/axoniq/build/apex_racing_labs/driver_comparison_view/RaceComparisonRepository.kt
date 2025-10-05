package io.axoniq.build.apex_racing_labs.driver_comparison_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for race comparison data.
 * Provides data access methods for race performance comparisons.
 */
@Repository
interface RaceComparisonRepository : JpaRepository<RaceComparisonEntity, Long> {

    /**
     * Finds race comparison by race ID and driver ID.
     */
    fun findByRaceIdAndDriverDriverId(raceId: String, driverId: String): RaceComparisonEntity?
    
    /**
     * Finds all race comparisons for a specific driver.
     */
    fun findByDriverDriverId(driverId: String): List<RaceComparisonEntity>

    /**
     * Finds race comparisons where both drivers participated.
     */
    @Query("SELECT rc FROM RaceComparisonEntity rc WHERE rc.raceId IN " +
           "(SELECT rc2.raceId FROM RaceComparisonEntity rc2 WHERE rc2.driver.driverId = :rivalDriverId) " +
           "AND rc.driver.driverId = :driverId")
    fun findHeadToHeadComparisons(@Param("driverId") driverId: String, 
                                 @Param("rivalDriverId") rivalDriverId: String): List<RaceComparisonEntity>
}

