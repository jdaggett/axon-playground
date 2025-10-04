package io.axoniq.build.jupiter_wheels.bikes_view

import io.axoniq.build.jupiter_wheels.bikes_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Bikes View component that handles bike-related queries and displays.
 * This component maintains a read model by listening to bike-related events
 * and provides query handlers for various bike queries.
 */
@Component
class BikesViewComponent(
    private val bikeRepository: BikeRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikesViewComponent::class.java)
    }

    /**
     * Query handler for SimilarNearbyBikes query.
     * Returns bikes similar to the requested type in the specified location.
     */
    @QueryHandler
    fun handle(query: SimilarNearbyBikes): SimilarNearbyBikesList {
        logger.info("Handling SimilarNearbyBikes query for location: ${query.location}, bikeType: ${query.bikeType}")

        val bikes = if (query.bikeType != null) {
            bikeRepository.findByLocationAndBikeType(query.location, query.bikeType)
        } else {
            bikeRepository.findByLocation(query.location)
        }

        val bikeItems = bikes.map { bike ->
            BikeItem(
                location = bike.location,
                bikeType = bike.bikeType,
                status = bike.status,
                bikeId = bike.bikeId
            )
        }
        
        return SimilarNearbyBikesList(bikeItems)
    }
    
    /**
     * Query handler for AvailableBikes query.
     * Returns available bikes, optionally filtered by location.
     */
    @QueryHandler
    fun handle(query: AvailableBikes): AvailableBikesList {
        logger.info("Handling AvailableBikes query for location: ${query.location}")

        val bikes = if (query.location != null) {
            bikeRepository.findByLocationAndStatus(query.location, "AVAILABLE")
        } else {
            bikeRepository.findByStatus("AVAILABLE")
        }

        val bikeItems = bikes.map { bike ->
            BikeItem(
                location = bike.location,
                bikeType = bike.bikeType,
                status = bike.status,
                bikeId = bike.bikeId
            )
        }

        return AvailableBikesList(bikeItems)
    }
    
    /**
     * Query handler for ReplacementBikeDetails query.
     * Returns details of a replacement bike by its ID.
     */
    @QueryHandler
    fun handle(query: ReplacementBikeDetails): ReplacementBikeDetailsResult {
        logger.info("Handling ReplacementBikeDetails query for bikeId: ${query.replacementBikeId}")
        
        val bike = bikeRepository.findById(query.replacementBikeId)
            .orElseThrow { IllegalArgumentException("Replacement bike not found: ${query.replacementBikeId}") }

        return ReplacementBikeDetailsResult(
            location = bike.location,
            bikeType = bike.bikeType,
            condition = bike.condition,
            bikeId = bike.bikeId
        )
    }

    /**
     * Query handler for AllBikesInFleet query.
     * Returns all bikes in the fleet.
     */
    @QueryHandler
    fun handle(query: AllBikesInFleet): AllBikesInFleetList {
        logger.info("Handling AllBikesInFleet query")
        
        val bikes = bikeRepository.findAll()

        val bikeItems = bikes.map { bike ->
            BikeItem(
                location = bike.location,
                bikeType = bike.bikeType,
                status = bike.status,
                bikeId = bike.bikeId
            )
        }

        return AllBikesInFleetList(bikeItems)
    }

    /**
     * Query handler for BikeDetails query.
     * Returns detailed information about a specific bike.
     */
    @QueryHandler
    fun handle(query: BikeDetails): BikeDetailsResult {
        logger.info("Handling BikeDetails query for bikeId: ${query.bikeId}")
        
        val bike = bikeRepository.findById(query.bikeId)
            .orElseThrow { IllegalArgumentException("Bike not found: ${query.bikeId}") }

        return BikeDetailsResult(
            location = bike.location,
            bikeType = bike.bikeType,
            userRating = bike.userRating,
            condition = bike.condition,
            bikeId = bike.bikeId
        )
    }

    /**
     * Query handler for BikeMaintenanceDetails query.
     * Returns maintenance details for a specific bike.
     */
    @QueryHandler
    fun handle(query: BikeMaintenanceDetails): BikeMaintenanceDetailsResult {
        logger.info("Handling BikeMaintenanceDetails query for bikeId: ${query.bikeId}")
        
        val bike = bikeRepository.findById(query.bikeId)
            .orElseThrow { IllegalArgumentException("Bike not found: ${query.bikeId}") }

        return BikeMaintenanceDetailsResult(
            maintenanceHistory = bike.maintenanceHistory.toList(),
            lastInspection = bike.lastInspection,
            condition = bike.condition,
            bikeId = bike.bikeId
        )
    }

    /**
     * Event handler for BikeRentalRequested event.
     * Updates bike status when a rental is requested.
     */
    @EventHandler
    fun on(event: BikeRentalRequested) {
        logger.info("Handling BikeRentalRequested event for bikeId: ${event.bikeId}")

        bikeRepository.findById(event.bikeId).ifPresent { bike ->
            val updatedBike = bike.copy(status = "RENTED")
            bikeRepository.save(updatedBike)
            logger.debug("Updated bike status to RENTED for bikeId: ${event.bikeId}")
        }
    }

    /**
     * Event handler for ReplacementBikeAssigned event.
     * Updates status for both original and replacement bikes.
     */
    @EventHandler
    fun on(event: ReplacementBikeAssigned) {
        logger.info("Handling ReplacementBikeAssigned event for originalBikeId: ${event.originalBikeId}, replacementBikeId: ${event.replacementBikeId}")

        // Update original bike status
        bikeRepository.findById(event.originalBikeId).ifPresent { originalBike ->
            val updatedOriginalBike = originalBike.copy(status = "OUT_OF_SERVICE")
            bikeRepository.save(updatedOriginalBike)
            logger.debug("Updated original bike status to OUT_OF_SERVICE for bikeId: ${event.originalBikeId}")
        }

        // Update replacement bike status
        bikeRepository.findById(event.replacementBikeId).ifPresent { replacementBike ->
            val updatedReplacementBike = replacementBike.copy(status = "RENTED")
            bikeRepository.save(updatedReplacementBike)
            logger.debug("Updated replacement bike status to RENTED for bikeId: ${event.replacementBikeId}")
        }
    }
    
    /**
     * Event handler for BikeCreated event.
     * Creates a new bike entry in the view when a bike is created.
     */
    @EventHandler
    fun on(event: BikeCreated) {
        logger.info("Handling BikeCreated event for bikeId: ${event.bikeId}")

        val bikeEntity = BikeEntity(
            bikeId = event.bikeId,
            location = event.location,
            bikeType = event.bikeType,
            condition = event.condition,
            status = "AVAILABLE"
        )

        bikeRepository.save(bikeEntity)
        logger.debug("Created new bike entity for bikeId: ${event.bikeId}")
    }

    /**
     * Event handler for RentalRequestRejectedTimeout event.
     * Updates bike status when rental request is rejected due to timeout.
     */
    @EventHandler
    fun on(event: RentalRequestRejectedTimeout) {
        logger.info("Handling RentalRequestRejectedTimeout event for bikeId: ${event.bikeId}")

        bikeRepository.findById(event.bikeId).ifPresent { bike ->
            val updatedBike = bike.copy(status = "AVAILABLE")
            bikeRepository.save(updatedBike)
            logger.debug("Updated bike status to AVAILABLE after timeout for bikeId: ${event.bikeId}")
        }
    }

    /**
     * Event handler for RentalRequestRejectedCancellation event.
     * Updates bike status when rental request is rejected due to cancellation.
     */
    @EventHandler
    fun on(event: RentalRequestRejectedCancellation) {
        logger.info("Handling RentalRequestRejectedCancellation event for bikeId: ${event.bikeId}")

        bikeRepository.findById(event.bikeId).ifPresent { bike ->
            val updatedBike = bike.copy(status = "AVAILABLE")
            bikeRepository.save(updatedBike)
            logger.debug("Updated bike status to AVAILABLE after cancellation for bikeId: ${event.bikeId}")
        }
    }
    
    /**
     * Event handler for BikeMarkedAsAvailable event.
     * Updates bike status when marked as available.
     */
    @EventHandler
    fun on(event: BikeMarkedAsAvailable) {
        logger.info("Handling BikeMarkedAsAvailable event for bikeId: ${event.bikeId}")

        bikeRepository.findById(event.bikeId).ifPresent { bike ->
            val updatedBike = bike.copy(status = "AVAILABLE")
            bikeRepository.save(updatedBike)
            logger.debug("Updated bike status to AVAILABLE for bikeId: ${event.bikeId}")
        }
    }

    /**
     * Event handler for BikeRemovedFromFleet event.
     * Removes or updates bike status when removed from fleet.
     */
    @EventHandler
    fun on(event: BikeRemovedFromFleet) {
        logger.info("Handling BikeRemovedFromFleet event for bikeId: ${event.bikeId}, reason: ${event.removalReason}")

        bikeRepository.findById(event.bikeId).ifPresent { bike ->
            val updatedBike = bike.copy(
                status = "REMOVED",
                maintenanceHistory = bike.maintenanceHistory.apply {
                    add("Removed from fleet: ${event.removalReason}")
                }
            )
            bikeRepository.save(updatedBike)
            logger.debug("Updated bike status to REMOVED for bikeId: ${event.bikeId}")
        }
    }
}

