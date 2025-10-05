package io.axoniq.build.jupiter_wheels.support_system

import io.axoniq.build.jupiter_wheels.support_system.api.PhotoFlaggedForReview
import io.axoniq.build.jupiter_wheels.support_system.api.ApproveOrRejectPhoto
import io.axoniq.build.jupiter_wheels.support_system.api.EmergencySupportContacted
import io.axoniq.build.jupiter_wheels.support_system.api.ProvideGPSLocation
import org.springframework.stereotype.Service
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Support System Integration - External System Component
 *
 * This component connects users with support agents by handling photo review requests
 * and emergency support scenarios. It processes events that require external support
 * intervention and coordinates responses back to the system.
 */
@Service
class SupportSystemIntegration(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(SupportSystemIntegration::class.java)

    /**
     * Review Photo - External Action Handler
     * 
     * Handles PhotoFlaggedForReview events by initiating a photo review process
     * with external support agents. Once reviewed, sends back an approval/rejection
     * decision to the system.
     * 
     * @param event The PhotoFlaggedForReview event containing photo URL and rental ID
     * @param processingContext The processing context for command gateway operations
     */
    @EventHandler
    fun reviewPhoto(event: PhotoFlaggedForReview, processingContext: ProcessingContext) {
        logger.info("Initiating photo review for rental ${event.rentalId}: ${event.photoUrl}")

        // Log the external system action that would be performed
        logger.info("External Action: Sending photo ${event.photoUrl} to support agents for manual review")

        // Simulate external support system decision and send response back
        // In a real implementation, this would involve external API calls or manual review processes
        val approveCommand = ApproveOrRejectPhoto(
            approved = true, // This would come from actual support agent decision
            rentalId = event.rentalId
        )

        commandGateway.send(approveCommand, processingContext)
        logger.info("Photo review command sent back to system for rental ${event.rentalId}")
    }

    /**
     * Connect User with Support - External Action Handler
     * 
     * Handles EmergencySupportContacted events by connecting users with appropriate
     * support channels and providing GPS location assistance when needed.
     * 
     * @param event The EmergencySupportContacted event containing emergency type and rental ID
     * @param processingContext The processing context for command gateway operations
     */
    @EventHandler
    fun connectUserWithSupport(event: EmergencySupportContacted, processingContext: ProcessingContext) {
        logger.info("Emergency support requested for rental ${event.rentalId}: ${event.emergencyType}")

        // Log the external system action that would be performed
        logger.info("External Action: Connecting user to emergency support for ${event.emergencyType}")

        // Send GPS location request back to help with emergency response
        // In a real implementation, this would coordinate with emergency services or support teams
        val gpsCommand = ProvideGPSLocation(
            latitude = 0.0, // This would be determined by support system requirements
            longitude = 0.0, // This would be determined by support system requirements
            rentalId = event.rentalId
        )

        commandGateway.send(gpsCommand, processingContext)
        logger.info("GPS location request sent back to system for rental ${event.rentalId}")
    }
}