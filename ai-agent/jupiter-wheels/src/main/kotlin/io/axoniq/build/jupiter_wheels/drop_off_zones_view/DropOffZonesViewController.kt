package io.axoniq.build.jupiter_wheels.drop_off_zones_view

import io.axoniq.build.jupiter_wheels.drop_off_zones_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Drop-off Zones View component.
 * Exposes HTTP endpoints for querying drop-off zone information.
 */
@RestController
@RequestMapping("/api/drop-off-zones")
class DropOffZonesViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DropOffZonesViewController::class.java)
    }

    /**
     * GET endpoint to retrieve available drop-off zones.
     * Optionally accepts a user location parameter.
     */
    @GetMapping("/available")
    fun getAvailableDropOffZones(
        @RequestParam(required = false) userLocation: String?
    ): CompletableFuture<AvailableDropOffZonesList> {
        logger.info("REST request for available drop-off zones with user location: $userLocation")

        val query = AvailableDropOffZones(userLocation = userLocation)
        return queryGateway.query(query, AvailableDropOffZonesList::class.java, null)
    }

    /**
     * GET endpoint to retrieve details of a specific drop-off zone.
     */
    @GetMapping("/{zoneId}")
    fun getZoneDetails(@PathVariable zoneId: String): CompletableFuture<ZoneDetailsResult?> {
        logger.info("REST request for zone details with zone ID: $zoneId")
        
        val query = ZoneDetails(zoneId = zoneId)
        return queryGateway.query(query, ZoneDetailsResult::class.java, null)
    }
}