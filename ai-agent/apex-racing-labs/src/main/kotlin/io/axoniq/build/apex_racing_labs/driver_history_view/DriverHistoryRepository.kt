package io.axoniq.build.apex_racing_labs.driver_history_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for accessing driver history data.
 * Used by the Driver Performance History View component to manage driver performance data.
 */
@Repository
interface DriverHistoryRepository : JpaRepository<DriverHistoryEntity, String>

/**
 * Repository interface for accessing driver race history data.
 * Used by the Driver Performance History View component to manage individual race performance data.
 */
@Repository
interface DriverRaceHistoryRepository : JpaRepository<DriverRaceHistoryEntity, Long> {
    fun findByDriverId(driverId: String): List<DriverRaceHistoryEntity>
    fun findByDriverIdAndRaceId(driverId: String, raceId: String): DriverRaceHistoryEntity?
}

