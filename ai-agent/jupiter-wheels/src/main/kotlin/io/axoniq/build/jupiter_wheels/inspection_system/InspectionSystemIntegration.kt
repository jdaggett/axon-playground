package io.axoniq.build.jupiter_wheels.inspection_system

import io.axoniq.build.jupiter_wheels.inspection_system.api.BikePhotoSubmitted
import io.axoniq.build.jupiter_wheels.inspection_system.api.ReportInspectionResults
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Inspection System Integration component that handles external bike inspection operations.
 * This component processes bike photo submissions and triggers inspection processes,
 * then reports the results back to the system.
 */
@Service
class InspectionSystemIntegration(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(InspectionSystemIntegration::class.java)

    /**
     * Handles BikePhotoSubmitted events to perform bike inspection.
     * This method is triggered when a bike photo is submitted for inspection,
     * processes the inspection externally, and sends the results back to the system.
     * 
     * @param event The BikePhotoSubmitted event containing the photo URL and rental ID
     * @param processingContext The processing context for command execution
     */
    @EventHandler
    fun performBikeInspection(event: BikePhotoSubmitted, processingContext: ProcessingContext) {
        logger.info("Performing bike inspection for rental: ${event.rentalId} with photo: ${event.photoUrl}")

        // TODO: Integrate with actual external inspection system
        // For now, simulate inspection logic
        val inspectionPassed = simulateInspection(event.photoUrl)
        val issues = if (inspectionPassed) null else "Minor scratches detected on frame"

        logger.info("Inspection completed for rental: ${event.rentalId}, passed: $inspectionPassed")

        // Report inspection results back to the system
        val reportCommand = ReportInspectionResults(
            inspectionPassed = inspectionPassed,
            issues = issues,
            rentalId = event.rentalId
        )

        commandGateway.send(reportCommand, processingContext)
    }

    /**
     * Simulates the external inspection process.
     * In a real implementation, this would integrate with an actual inspection service.
     * 
     * @param photoUrl The URL of the bike photo to inspect
     * @return Boolean indicating whether the inspection passed
     */
    private fun simulateInspection(photoUrl: String): Boolean {
        // Simulate inspection logic - in reality this would call an external AI service
        // For demonstration purposes, randomly determine if inspection passes
        return photoUrl.isNotEmpty() && System.currentTimeMillis() % 3 != 0L
    }
}