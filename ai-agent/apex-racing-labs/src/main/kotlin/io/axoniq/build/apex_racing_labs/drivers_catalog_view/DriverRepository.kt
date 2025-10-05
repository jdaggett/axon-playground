package io.axoniq.build.apex_racing_labs.drivers_catalog_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.*

/**
 * JPA repository for accessing driver entities in the drivers catalog view.
 * Component: Drivers Catalog View
 */
@Repository
interface DriverRepository : JpaRepository<DriverEntity, String> {
    
    /**
     * Find all active drivers for the available drivers query.
     * @return List of active driver entities
     */
    @Query("SELECT d FROM DriverEntity d WHERE d.active = true")
    fun findAllActiveDrivers(): List<DriverEntity>

    /**
     * Find driver by ID for driver details query.
     * @param driverId The driver identifier
     * @return Optional containing the driver entity if found
     */
    override fun findById(driverId: String): Optional<DriverEntity>
}

