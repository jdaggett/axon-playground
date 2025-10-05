package io.axoniq.build.jupiter_wheels.bikes_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for bike entities in the Bikes View component.
 * Provides data access methods for bike-related queries.
 */
@Repository
interface BikeRepository : JpaRepository<BikeEntity, String> {
    
    /**
     * Find bikes by location, optionally filtered by bike type.
     */
    fun findByLocationAndBikeType(location: String, bikeType: String): List<BikeEntity>
    
    /**
     * Find bikes by location only.
     */
    fun findByLocation(location: String): List<BikeEntity>

    /**
     * Find available bikes by location.
     */
    fun findByLocationAndStatus(location: String, status: String): List<BikeEntity>

    /**
     * Find all bikes with specific status.
     */
    fun findByStatus(status: String): List<BikeEntity>
}

