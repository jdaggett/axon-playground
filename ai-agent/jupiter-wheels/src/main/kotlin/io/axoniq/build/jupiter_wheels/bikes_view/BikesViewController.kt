package io.axoniq.build.jupiter_wheels.bikes_view

import io.axoniq.build.jupiter_wheels.bikes_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for the Bikes View component.
 * Exposes HTTP endpoints for bike-related queries using the QueryGateway.
 */
@RestController
@RequestMapping("/api/bikes")
class BikesViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikesViewController::class.java)
    }

    /**
     * Get similar nearby bikes endpoint.
     */
    @GetMapping("/similar")
    fun getSimilarNearbyBikes(
        @RequestParam location: String,
        @RequestParam(required = false) bikeType: String?
    ): CompletableFuture<SimilarNearbyBikesList> {
        logger.info("REST request for similar nearby bikes at location: $location, bikeType: $bikeType")
        val query = SimilarNearbyBikes(location, bikeType)
        return queryGateway.query(query, SimilarNearbyBikesList::class.java, null)
    }

    /**
     * Get available bikes endpoint.
     */
    @GetMapping("/available")
    fun getAvailableBikes(@RequestParam(required = false) location: String?): CompletableFuture<AvailableBikesList> {
        logger.info("REST request for available bikes at location: $location")
        val query = AvailableBikes(location)
        return queryGateway.query(query, AvailableBikesList::class.java, null)
    }

    /**
     * Get replacement bike details endpoint.
     */
    @GetMapping("/replacement/{replacementBikeId}")
    fun getReplacementBikeDetails(@PathVariable replacementBikeId: String): CompletableFuture<ReplacementBikeDetailsResult> {
        logger.info("REST request for replacement bike details: $replacementBikeId")
        val query = ReplacementBikeDetails(replacementBikeId)
        return queryGateway.query(query, ReplacementBikeDetailsResult::class.java, null)
    }

    /**
     * Get all bikes in fleet endpoint.
     */
    @GetMapping("/fleet")
    fun getAllBikesInFleet(): CompletableFuture<AllBikesInFleetList> {
        logger.info("REST request for all bikes in fleet")
        val query = AllBikesInFleet()
        return queryGateway.query(query, AllBikesInFleetList::class.java, null)
    }

    /**
     * Get bike details endpoint.
     */
    @GetMapping("/{bikeId}")
    fun getBikeDetails(@PathVariable bikeId: String): CompletableFuture<BikeDetailsResult> {
        logger.info("REST request for bike details: $bikeId")
        val query = BikeDetails(bikeId)
        return queryGateway.query(query, BikeDetailsResult::class.java, null)
    }

    /**
     * Get bike maintenance details endpoint.
     */
    @GetMapping("/{bikeId}/maintenance")
    fun getBikeMaintenanceDetails(@PathVariable bikeId: String): CompletableFuture<BikeMaintenanceDetailsResult> {
        logger.info("REST request for bike maintenance details: $bikeId")
        val query = BikeMaintenanceDetails(bikeId)
        return queryGateway.query(query, BikeMaintenanceDetailsResult::class.java, null)
    }
}