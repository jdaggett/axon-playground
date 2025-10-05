package io.axoniq.build.apex_racing_labs.driver_comparison_view

import io.axoniq.build.apex_racing_labs.driver_comparison_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for Driver Comparison View component.
 * Provides HTTP endpoints for accessing driver comparison analytics.
 */
@RestController
@RequestMapping("/api/driver-comparison")
class DriverComparisonViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverComparisonViewController::class.java)
    }

    /**
     * REST endpoint for retrieving driver profile information.
     * Returns driver details including overall rating and team information.
     */
    @GetMapping("/profile/{driverId}")
    fun getDriverProfile(@PathVariable driverId: String): CompletableFuture<DriverProfileResult> {
        logger.info("REST request for driver profile: {}", driverId)
        val query = DriverProfile(driverId)
        return queryGateway.query(query, DriverProfileResult::class.java, null)
    }

    /**
     * REST endpoint for retrieving driver comparison charts.
     * Returns head-to-head race comparisons between two drivers.
     */
    @GetMapping("/comparison/{driverId}/vs/{rivalDriverId}")
    fun getDriverComparison(
        @PathVariable driverId: String,
        @PathVariable rivalDriverId: String
    ): CompletableFuture<DriverComparisonResult> {
        logger.info("REST request for driver comparison: {} vs {}", driverId, rivalDriverId)
        val query = DriverComparisonCharts(driverId, rivalDriverId)
        return queryGateway.query(query, DriverComparisonResult::class.java, null)
    }
}