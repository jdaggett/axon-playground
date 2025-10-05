package io.axoniq.build.jupiter_wheels.bike_replacement_management

import io.axoniq.build.jupiter_wheels.bike_replacement_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Bike Replacement Management component.
 * Handles bike replacement requests and assignments.
 */
class BikeReplacementManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeReplacementManagementCommandHandler::class.java)
    }

    /**
     * Handles the RequestBikeReplacement command.
     * Creates a bike replacement request when a bike has issues.
     */
    @CommandHandler
    fun handle(
        command: RequestBikeReplacement,
        @InjectEntity state: BikeReplacementManagementState,
        eventAppender: EventAppender
    ): BikeReplacementRequestResult {
        logger.info("Processing RequestBikeReplacement command for originalBikeId: ${command.originalBikeId}")
        
        val event = BikeReplacementRequested(
            originalBikeId = command.originalBikeId,
            rentalId = command.rentalId,
            issueDescription = command.issueDescription
        )

        eventAppender.append(event)

        val replacementRequestId = UUID.randomUUID().toString()
        logger.info("Bike replacement requested for originalBikeId: ${command.originalBikeId}, replacementRequestId: $replacementRequestId")

        return BikeReplacementRequestResult(replacementRequestId = replacementRequestId)
    }

    /**
     * Handles the AssignReplacementBike command.
     * Assigns a replacement bike to an existing rental.
     */
    @CommandHandler
    fun handle(
        command: AssignReplacementBike,
        @InjectEntity state: BikeReplacementManagementState,
        eventAppender: EventAppender
    ): BikeReplacementAssignmentResult {
        logger.info("Processing AssignReplacementBike command for rentalId: ${command.rentalId}")

        if (state.getOriginalBikeId() == null) {
            logger.error("No bike replacement request found for rentalId: ${command.rentalId}")
            throw IllegalStateException("No bike replacement request found for this rental")
        }
        
        val event = ReplacementBikeAssigned(
            originalBikeId = state.getOriginalBikeId()!!,
            replacementBikeId = command.replacementBikeId,
            rentalId = command.rentalId
        )

        eventAppender.append(event)
        
        logger.info("Replacement bike assigned for rentalId: ${command.rentalId}, replacementBikeId: ${command.replacementBikeId}")

        return BikeReplacementAssignmentResult(assignmentConfirmed = true)
    }
}

