package io.axoniq.build.jupiter_wheels.drop_off_zones_view

import io.axoniq.build.jupiter_wheels.drop_off_zones_view.api.*
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Drop-off Zones View component that handles location and zone queries.
 * This component provides query handlers for retrieving available drop-off zones
 * and specific zone details.
 */
@Component
class DropOffZonesViewComponent(
    private val dropOffZoneRepository: DropOffZoneRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DropOffZonesViewComponent::class.java)
    }

    /**
     * Query handler for AvailableDropOffZones query.
     * Returns a list of available drop-off zones based on the user location.
     * Currently returns all zones with available spaces regardless of user location.
     */
    @QueryHandler
    fun handle(query: AvailableDropOffZones): AvailableDropOffZonesList {
        logger.info("Handling AvailableDropOffZones query for user location: ${query.userLocation}")

        // Find all zones with available spaces (> 0)
        val availableZones = dropOffZoneRepository.findByAvailableSpacesGreaterThan(0)

        // Convert entities to API objects
        val zones = availableZones.map { entity ->
            DropOffZone(
                location = entity.location,
                availableSpaces = entity.availableSpaces,
                zoneName = entity.zoneName,
                zoneId = entity.zoneId
            )
        }

        logger.debug("Found ${zones.size} available drop-off zones")
        return AvailableDropOffZonesList(zones = zones)
    }

    /**
     * Query handler for ZoneDetails query.
     * Returns detailed information about a specific drop-off zone.
     */
    @QueryHandler
    fun handle(query: ZoneDetails): ZoneDetailsResult? {
        logger.info("Handling ZoneDetails query for zone ID: ${query.zoneId}")

        val zoneEntity = dropOffZoneRepository.findById(query.zoneId).orElse(null)

        return if (zoneEntity != null) {
            logger.debug("Found zone details for zone ID: ${query.zoneId}")
            ZoneDetailsResult(
                location = zoneEntity.location,
                capacity = zoneEntity.capacity,
                availableSpaces = zoneEntity.availableSpaces,
                zoneName = zoneEntity.zoneName,
                zoneId = zoneEntity.zoneId
            )
        } else {
            logger.warn("Zone not found for zone ID: ${query.zoneId}")
            null
        }
    }
}