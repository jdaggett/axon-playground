package io.axoniq.build.apex_racing_labs.drivers_catalog_view

import io.axoniq.build.apex_racing_labs.drivers_catalog_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the drivers catalog view.
 * Exposes HTTP endpoints to query driver information.
 * Component: Drivers Catalog View
 */
@RestController
@RequestMapping("/api/drivers-catalog")
class DriversCatalogViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriversCatalogViewController::class.java)
    }

    /**
     * REST endpoint to retrieve all available drivers.
     * Returns a list of active drivers with their basic information.
     * Component: Drivers Catalog View
     * 
     * @return CompletableFuture containing the available drivers result
     */
    @GetMapping("/available")
    fun getAvailableDrivers(): CompletableFuture<AvailableDriversResult> {
        logger.info("REST request for available drivers")

        val query = AvailableDrivers()
        return queryGateway.query(query, AvailableDriversResult::class.java, null)
    }

    /**
     * REST endpoint to retrieve specific driver details.
     * Returns detailed information about a specific driver including active status.
     * Component: Drivers Catalog View
     * 
     * @param driverId The driver identifier from the path variable
     * @return CompletableFuture containing the driver details result
     */
    @GetMapping("/{driverId}")
    fun getDriverDetails(@PathVariable driverId: String): CompletableFuture<DriverDetailsResult> {
        logger.info("REST request for driver details: {}", driverId)

        val query = DriverDetails(driverId = driverId)
        return queryGateway.query(query, DriverDetailsResult::class.java, null)
    }
}