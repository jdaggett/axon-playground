package io.axoniq.build.jupiter_wheels.drop_off_zones_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for Drop-off Zones View component.
 * Provides data access methods for querying drop-off zone entities.
 */
@Repository
interface DropOffZoneRepository : JpaRepository<DropOffZoneEntity, String> {
    
    /**
     * Finds all drop-off zones that have available spaces (> 0).
     * Used for querying available drop-off zones.
     */
    fun findByAvailableSpacesGreaterThan(spaces: Int): List<DropOffZoneEntity>
}

