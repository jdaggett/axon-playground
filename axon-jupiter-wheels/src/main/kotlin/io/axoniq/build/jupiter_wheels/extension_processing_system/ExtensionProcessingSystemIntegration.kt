package io.axoniq.build.jupiter_wheels.extension_processing_system

import io.axoniq.build.jupiter_wheels.extension_processing_system.api.RentalExtensionRequested
import io.axoniq.build.jupiter_wheels.extension_processing_system.api.ApproveRentalExtension
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Extension Processing System Integration - External System Component
 * 
 * This component processes rental extension requests by integrating with an external
 * extension processing system. When a rental extension is requested, it processes
 * the request and sends back an approval command to the main system.
 */
@Service
class ExtensionProcessingSystemIntegration(
    private val commandGateway: CommandGateway
) {
    private val logger: Logger = LoggerFactory.getLogger(ExtensionProcessingSystemIntegration::class.java)

    /**
     * Handles RentalExtensionRequested events by processing the extension request
     * through the external extension processing system.
     * 
     * This method logs the processing action and sends an ApproveRentalExtension
     * command back to the main system with the approved time.
     * 
     * @param event The RentalExtensionRequested event containing rental ID and additional time
     * @param processingContext The processing context for command execution
     */
    @EventHandler
    fun processRequest(event: RentalExtensionRequested, processingContext: ProcessingContext) {
        logger.info("Processing rental extension request for rental ${event.rentalId} with additional time ${event.additionalTime} minutes")

        // In a real implementation, this would integrate with an external system
        // For now, we simulate the processing and approve the requested time
        val approvedTime = event.additionalTime

        logger.info("Extension request processed, approving ${approvedTime} minutes for rental ${event.rentalId}")

        // Send approval command back to the main system
        val approvalCommand = ApproveRentalExtension(
            rentalId = event.rentalId,
            approvedTime = approvedTime
        )
        
        commandGateway.send(approvalCommand, processingContext)
        logger.debug("ApproveRentalExtension command sent for rental ${event.rentalId}")
    }
}