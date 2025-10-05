package io.axoniq.build.apex_racing_labs.driver_management

import io.axoniq.build.apex_racing_labs.driver_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for Driver Management Service.
 * Exposes HTTP endpoints for driver management operations.
 */
@RestController
@RequestMapping("/api/drivers")
class DriverManagementServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(DriverManagementServiceController::class.java)
    }

    /**
     * Creates a new driver in the system.
     * 
     * @param request The CreateDriverRequest containing driver details
     * @return ResponseEntity with success or error message
     */
    @PostMapping
    fun createDriver(@RequestBody request: CreateDriverRequest): ResponseEntity<String> {
        val command = CreateDriver(
            teamId = request.teamId,
            driverId = request.driverId,
            driverName = request.driverName
        )
        logger.info("Dispatching CreateDriver command: $command")

        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Driver creation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch CreateDriver command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to create driver")
        }
    }

    /**
     * Removes an existing driver from the system.
     * 
     * @param driverId The ID of the driver to remove
     * @return ResponseEntity with success or error message
     */
    @DeleteMapping("/{driverId}")
    fun removeDriver(@PathVariable driverId: String): ResponseEntity<String> {
        val command = RemoveDriver(driverId = driverId)
        logger.info("Dispatching RemoveDriver command: $command")
        
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Driver removal accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RemoveDriver command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove driver")
        }
    }

    /**
     * Request data class for creating a driver.
     */
    data class CreateDriverRequest(
        val teamId: String,
        val driverId: String,
        val driverName: String
    )
}

