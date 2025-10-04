package io.axoniq.build.jupiter_wheels.bike_monitoring_system

import io.axoniq.build.jupiter_wheels.bike_monitoring_system.api.BikeMarkedAsInUse
import io.axoniq.build.jupiter_wheels.bike_monitoring_system.api.RequestBikeReplacement
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Bike Monitoring System Integration - External System Component
 * 
 * This component monitors bike conditions and detects issues when bikes are marked as in use.
 * It acts as an integration point with an external bike monitoring system that can detect
 * mechanical issues, battery problems, or other conditions that require bike replacement.
 *
 * When a bike is marked as in use, this component simulates monitoring the bike's condition
 * and may trigger a bike replacement request if issues are detected.
 */
@Service
class BikeMonitoringSystemIntegration(
    private val commandGateway: CommandGateway
) {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeMonitoringSystemIntegration::class.java)
    }

    /**
     * Handles the BikeMarkedAsInUse event to detect potential issues with the bike.
     * 
     * This method simulates an external monitoring system that would analyze bike conditions
     * when it starts being used. In a real implementation, this would integrate with actual
     * IoT sensors, maintenance databases, or other monitoring systems.
     * 
     * For demonstration purposes, this implementation logs the monitoring action and 
     * sends a bike replacement request command back to the system.
     * 
     * @param event The BikeMarkedAsInUse event containing bike and rental information
     * @param processingContext The processing context for command execution
     */
    @EventHandler
    fun detectIssue(event: BikeMarkedAsInUse, processingContext: ProcessingContext) {
        logger.info("Bike Monitoring System: Starting condition monitoring for bike {} in rental {}", 
                   event.bikeId, event.rentalId)

        // Simulate external monitoring system detecting an issue
        logger.info("Bike Monitoring System: Analyzing bike condition for bike {}", event.bikeId)
        logger.info("Bike Monitoring System: Issue detected - requesting bike replacement")

        // Create and send replacement request command
        val replacementRequest = RequestBikeReplacement(
            originalBikeId = event.bikeId,
            rentalId = event.rentalId,
            issueDescription = "Bike monitoring system detected potential mechanical issue requiring replacement"
        )
        
        logger.info("Bike Monitoring System: Sending bike replacement request for bike {} in rental {}", 
                   event.bikeId, event.rentalId)

        commandGateway.send(replacementRequest, processingContext)
    }
}