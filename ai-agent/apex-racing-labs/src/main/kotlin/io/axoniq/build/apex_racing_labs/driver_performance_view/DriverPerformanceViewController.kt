package io.axoniq.build.apex_racing_labs.driver_performance_view

import io.axoniq.build.apex_racing_labs.driver_performance_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for accessing driver performance data.
 * Provides HTTP endpoints to query driver race performance information.
 */
@RestController
@RequestMapping("/api/driver-performance")
class DriverPerformanceViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverPerformanceViewController::class.java)
    }

    /**
     * Get driver performance data for a specific race.
     * Returns comprehensive performance information including ratings and averages.
     */
    @GetMapping("/{driverId}/race/{raceId}")
    fun getDriverRacePerformance(
        @PathVariable driverId: String,
        @PathVariable raceId: String
    ): CompletableFuture<DriverRacePerformanceResult> {
        logger.info("REST request for driver performance - driverId: $driverId, raceId: $raceId")
        
        val query = DriverRacePerformance(driverId, raceId)
        return queryGateway.query(query, DriverRacePerformanceResult::class.java, null)
    }
}