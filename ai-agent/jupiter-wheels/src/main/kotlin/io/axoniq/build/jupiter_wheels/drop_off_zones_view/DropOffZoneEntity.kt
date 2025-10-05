package io.axoniq.build.jupiter_wheels.drop_off_zones_view

import jakarta.persistence.*

/**
 * JPA Entity representing a drop-off zone in the Drop-off Zones View component.
 * This entity stores the state of drop-off zones including location, capacity,
 * available spaces, zone name, and zone identifier.
 */
@Entity
@Table(name = "drop_off_zones")
data class DropOffZoneEntity(
    @Id
    @Column(name = "zone_id")
    val zoneId: String,

    @Column(name = "location", nullable = false)
    val location: String,

    @Column(name = "capacity", nullable = false)
    val capacity: Int,

    @Column(name = "available_spaces", nullable = false)
    val availableSpaces: Int,

    @Column(name = "zone_name", nullable = false)
    val zoneName: String
) {
    // No-arg constructor required by JPA
    constructor() : this("", "", 0, 0, "")
}

