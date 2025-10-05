package io.axoniq.build.apex_racing_labs.driver_management

import io.axoniq.build.apex_racing_labs.driver_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for Driver Management Service.
 * Handles driver creation and removal operations.
 */
class DriverManagementServiceCommandHandler {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverManagementServiceCommandHandler::class.java)
    }

    /**
     * Handles CreateDriver command to create a new driver in the system.
     * 
     * @param command The CreateDriver command containing driver details
     * @param state The injected driver state entity
     * @param eventAppender Event appender to publish events
     * @return DriverCreationResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: CreateDriver,
        @InjectEntity state: DriverManagementServiceState,
        eventAppender: EventAppender
    ): DriverCreationResult {
        logger.info("Handling CreateDriver command for driverId: ${command.driverId}")
        
        return if (state.isActive()) {
            logger.warn("Driver with id ${command.driverId} already exists")
            DriverCreationResult(false, "Driver already exists")
        } else {
            val event = DriverCreated(
                teamId = command.teamId,
                driverId = command.driverId,
                driverName = command.driverName
            )
            eventAppender.append(event)
            logger.info("Driver created successfully with id: ${command.driverId}")
            DriverCreationResult(true, "Driver created successfully")
        }
    }

    /**
     * Handles RemoveDriver command to remove an existing driver from the system.
     * 
     * @param command The RemoveDriver command containing driver id
     * @param state The injected driver state entity
     * @param eventAppender Event appender to publish events
     * @return DriverRemovalResult indicating success or failure
     */
    @CommandHandler
    fun handle(
        command: RemoveDriver,
        @InjectEntity state: DriverManagementServiceState,
        eventAppender: EventAppender
    ): DriverRemovalResult {
        logger.info("Handling RemoveDriver command for driverId: ${command.driverId}")

        return if (!state.isActive()) {
            logger.warn("Driver with id ${command.driverId} does not exist or is already removed")
            DriverRemovalResult(false, "Driver does not exist")
        } else {
            val event = DriverRemoved(driverId = command.driverId)
            eventAppender.append(event)
            logger.info("Driver removed successfully with id: ${command.driverId}")
            DriverRemovalResult(true, "Driver removed successfully")
        }
    }
}

