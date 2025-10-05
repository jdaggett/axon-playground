package io.axoniq.build.apex_racing_labs.drivers_catalog_view

import io.axoniq.build.apex_racing_labs.drivers_catalog_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * View component for the drivers catalog that handles queries and events.
 * This component maintains a read model of available drivers and their details.
 * Component: Drivers Catalog View
 */
@Component
class DriversCatalogViewComponent(
    private val driverRepository: DriverRepository
) {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriversCatalogViewComponent::class.java)
    }

    /**
     * Query handler for retrieving all available drivers.
     * Returns a list of active drivers with their basic information.
     * Component: Drivers Catalog View
     * 
     * @param query The available drivers query
     * @return Result containing list of available driver information
     */
    @QueryHandler
    fun handle(query: AvailableDrivers): AvailableDriversResult {
        logger.info("Handling AvailableDrivers query")

        val activeDrivers = driverRepository.findAllActiveDrivers()
        val driverInfoList = activeDrivers.map { driver ->
            DriverInfo(
                teamId = driver.teamId,
                driverId = driver.driverId,
                teamName = driver.teamName,
                driverName = driver.driverName
            )
        }

        logger.debug("Retrieved {} active drivers", driverInfoList.size)
        return AvailableDriversResult(drivers = driverInfoList)
    }

    /**
     * Query handler for retrieving specific driver details.
     * Returns detailed information about a specific driver including active status.
     * Component: Drivers Catalog View
     * 
     * @param query The driver details query containing the driver ID
     * @return Result containing detailed driver information
     */
    @QueryHandler
    fun handle(query: DriverDetails): DriverDetailsResult? {
        logger.info("Handling DriverDetails query for driver: {}", query.driverId)

        val driverEntity = driverRepository.findById(query.driverId)

        return if (driverEntity.isPresent) {
            val driver = driverEntity.get()
            logger.debug("Found driver details for: {}", query.driverId)
            DriverDetailsResult(
                teamId = driver.teamId,
                driverId = driver.driverId,
                active = driver.active,
                teamName = driver.teamName,
                driverName = driver.driverName
            )
        } else {
            logger.warn("Driver not found: {}", query.driverId)
            null
        }
    }

    /**
     * Event handler for driver creation events.
     * Creates a new driver entry in the read model when a driver is created.
     * Component: Drivers Catalog View
     * 
     * @param event The driver created event
     */
    @EventHandler
    fun on(event: DriverCreated) {
        logger.info("Handling DriverCreated event for driver: {}", event.driverId)
        
        // Find team name - for now using teamId as teamName since team name is not in the event
        val teamName = event.teamId // This could be enhanced to lookup actual team name

        val driverEntity = DriverEntity(
            driverId = event.driverId,
            teamId = event.teamId,
            active = true,
            teamName = teamName,
            driverName = event.driverName
        )

        driverRepository.save(driverEntity)
        logger.info("Created driver entry for: {}", event.driverId)
    }

    /**
     * Event handler for driver removal events.
     * Marks a driver as inactive when they are removed from the system.
     * Component: Drivers Catalog View
     * 
     * @param event The driver removed event
     */
    @EventHandler
    fun on(event: DriverRemoved) {
        logger.info("Handling DriverRemoved event for driver: {}", event.driverId)
        
        val driverEntity = driverRepository.findById(event.driverId)

        if (driverEntity.isPresent) {
            val driver = driverEntity.get()
            val updatedDriver = driver.copy(active = false)
            driverRepository.save(updatedDriver)
            logger.info("Marked driver as inactive: {}", event.driverId)
        } else {
            logger.warn("Driver not found for removal: {}", event.driverId)
        }
    }
}

