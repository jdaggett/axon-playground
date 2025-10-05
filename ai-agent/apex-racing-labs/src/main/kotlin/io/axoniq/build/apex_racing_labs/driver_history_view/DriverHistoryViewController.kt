package io.axoniq.build.apex_racing_labs.driver_history_view

import io.axoniq.build.apex_racing_labs.driver_history_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for the Driver Performance History View component.
 * Exposes HTTP endpoints to query driver performance history and best races.
 */
@RestController
@RequestMapping("/api/driver-history")
class DriverHistoryViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverHistoryViewController::class.java)
    }

    /**
     * Retrieves driver performance history including total ratings, best races, and overall average rating.
     * 
     * @param driverId The unique identifier of the driver
     * @return Driver performance history data
     */
    @GetMapping("/{driverId}")
    fun getDriverHistory(@PathVariable driverId: String): CompletableFuture<DriverHistoryResult> {
        logger.info("Received request to get driver history for driver: $driverId")
        val query = DriverPerformanceHistory(driverId)
        return queryGateway.query(query, DriverHistoryResult::class.java, null)
    }
}