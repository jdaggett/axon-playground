package io.axoniq.build.apex_racing_labs.drivers_catalog_view

import jakarta.persistence.*
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * JPA entity representing a driver in the drivers catalog view.
 * This entity stores driver information including team details and active status.
 * Component: Drivers Catalog View
 */
@Entity
@Table(name = "drivers_catalog")
data class DriverEntity(
    @Id
    @Column(name = "driver_id", nullable = false)
    val driverId: String = "",

    @Column(name = "team_id", nullable = false)
    val teamId: String = "",

    @Column(name = "active", nullable = false)
    val active: Boolean = true,

    @Column(name = "team_name", nullable = false)
    val teamName: String = "",

    @Column(name = "driver_name", nullable = false)
    val driverName: String = ""
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverEntity::class.java)
    }
}

