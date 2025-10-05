package io.axoniq.build.jupiter_wheels.bikes_view

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA entity representing a bike in the Bikes View component.
 * This entity stores the read model data for bike-related queries.
 */
@Entity
@Table(name = "bikes")
data class BikeEntity(
    @Id
    val bikeId: String = "",

    val location: String = "",

    val bikeType: String = "",
    
    val userRating: Double? = null,

    val condition: String = "",
    
    val status: String = "",

    @ElementCollection
    @CollectionTable(name = "bike_maintenance_history", joinColumns = [JoinColumn(name = "bike_id")])
    @Column(name = "maintenance_record")
    val maintenanceHistory: MutableList<String> = mutableListOf(),

    val lastInspection: LocalDateTime? = null
)

