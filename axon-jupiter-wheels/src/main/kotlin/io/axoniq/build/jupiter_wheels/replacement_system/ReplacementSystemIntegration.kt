package io.axoniq.build.jupiter_wheels.replacement_system

import io.axoniq.build.jupiter_wheels.replacement_system.api.BikeReplacementRequested
import io.axoniq.build.jupiter_wheels.replacement_system.api.AssignReplacementBike
import org.springframework.stereotype.Service
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Replacement System Integration component
 *
 * This external system component handles bike replacement requests by finding and assigning
 * replacement bikes when a bike replacement is requested. It integrates with an external
 * bike inventory system to locate the nearest available replacement bike.
 */
@Service
class ReplacementSystemIntegration(
    private val commandGateway: CommandGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ReplacementSystemIntegration::class.java)
    }

    /**
     * Handles BikeReplacementRequested events to find and assign a replacement bike.
     * 
     * This method processes bike replacement requests by:
     * 1. Logging the replacement request details
     * 2. Finding the nearest available replacement bike (external system integration)
     * 3. Sending an AssignReplacementBike command back to the system
     * 
     * @param event The BikeReplacementRequested event containing the original bike ID, rental ID, and issue description
     * @param processingContext The processing context for command execution
     */
    @EventHandler
    fun handle(event: BikeReplacementRequested, processingContext: ProcessingContext) {
        logger.info("Processing bike replacement request for rental ${event.rentalId}. Original bike: ${event.originalBikeId}, Issue: ${event.issueDescription}")

        // External system integration: Find nearest available replacement bike
        logger.info("Finding nearest replacement bike for rental ${event.rentalId}")

        // Simulate finding a replacement bike ID (in real implementation, this would call external system)
        val replacementBikeId = findNearestReplacementBike(event.originalBikeId, event.rentalId)

        logger.info("Found replacement bike ${replacementBikeId} for rental ${event.rentalId}")

        // Send command back to the system to assign the replacement bike
        val assignCommand = AssignReplacementBike(
            replacementBikeId = replacementBikeId,
            rentalId = event.rentalId
        )

        commandGateway.send(assignCommand, processingContext)
        logger.info("Sent AssignReplacementBike command for rental ${event.rentalId} with replacement bike ${replacementBikeId}")
    }

    /**
     * Simulates external system call to find the nearest replacement bike.
     * In a real implementation, this would integrate with an external bike inventory system.
     * 
     * @param originalBikeId The ID of the original bike that needs replacement
     * @param rentalId The rental ID for context
     * @return The ID of the replacement bike found
     */
    private fun findNearestReplacementBike(originalBikeId: String, rentalId: String): String {
        // Stub implementation - in reality this would call external system APIs
        logger.debug("Calling external bike inventory system to find replacement for bike ${originalBikeId}")

        // Generate a mock replacement bike ID for demonstration
        return "REPL-${originalBikeId}-${System.currentTimeMillis() % 10000}"
    }
}