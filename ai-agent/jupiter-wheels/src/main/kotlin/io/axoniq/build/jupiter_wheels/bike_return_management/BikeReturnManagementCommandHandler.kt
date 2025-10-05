package io.axoniq.build.jupiter_wheels.bike_return_management

import io.axoniq.build.jupiter_wheels.bike_return_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Command handler for the Bike Return Management component.
 * Handles bike return process and validation commands.
 */
class BikeReturnManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeReturnManagementCommandHandler::class.java)
    }

    /**
     * Handles the ApproveOrRejectPhoto command.
     * Processes photo approval/rejection and triggers bike inspection completion if approved.
     */
    @CommandHandler
    fun handle(
        command: ApproveOrRejectPhoto,
        @InjectEntity state: BikeReturnState,
        eventAppender: EventAppender
    ): PhotoApprovalResult {
        logger.info("Handling ApproveOrRejectPhoto command for rental: {}", command.rentalId)

        if (command.approved && state.getPhotoSubmitted()) {
            val event = BikeInspectionCompleted(
                inspectionPassed = true,
                rentalId = command.rentalId,
                bikeId = state.getBikeId()!!
            )
            eventAppender.append(event)
        }

        return PhotoApprovalResult(approvalProcessed = true)
    }

    /**
     * Handles the ReportInspectionResults command.
     * Reports inspection results and marks bike as available if inspection passed.
     */
    @CommandHandler
    fun handle(
        command: ReportInspectionResults,
        @InjectEntity state: BikeReturnState,
        eventAppender: EventAppender
    ): InspectionResult {
        logger.info("Handling ReportInspectionResults command for rental: {}", command.rentalId)
        
        val inspectionEvent = BikeInspectionCompleted(
            inspectionPassed = command.inspectionPassed,
            rentalId = command.rentalId,
            bikeId = state.getBikeId()!!
        )
        eventAppender.append(inspectionEvent)

        if (command.inspectionPassed) {
            val availableEvent = BikeMarkedAsAvailable(
                bikeId = state.getBikeId()!!
            )
            eventAppender.append(availableEvent)
        }

        return InspectionResult(inspectionProcessed = true)
    }

    /**
     * Handles the SubmitBikePhoto command.
     * Processes bike photo submission and may flag it for review.
     */
    @CommandHandler
    fun handle(
        command: SubmitBikePhoto,
        @InjectEntity state: BikeReturnState,
        eventAppender: EventAppender
    ): BikePhotoResult {
        logger.info("Handling SubmitBikePhoto command for rental: {}", command.rentalId)

        val photoSubmittedEvent = BikePhotoSubmitted(
            photoUrl = command.photoUrl,
            rentalId = command.rentalId
        )
        eventAppender.append(photoSubmittedEvent)

        // Flag for review (simplified logic - in real scenario this could be AI-based)
        val flaggedEvent = PhotoFlaggedForReview(
            photoUrl = command.photoUrl,
            rentalId = command.rentalId
        )
        eventAppender.append(flaggedEvent)
        
        return BikePhotoResult(photoAccepted = true)
    }

    /**
     * Handles the SubmitReturnSurvey command.
     * Processes return survey submission based on the scenario where bike has been returned.
     */
    @CommandHandler
    fun handle(
        command: SubmitReturnSurvey,
        @InjectEntity state: BikeReturnState,
        eventAppender: EventAppender
    ): ReturnSurveyResult {
        logger.info("Handling SubmitReturnSurvey command for rental: {}", command.rentalId)
        
        val event = ReturnSurveySubmitted(
            feedback = command.feedback,
            rating = command.rating,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        return ReturnSurveyResult(surveyAccepted = true)
    }

    /**
     * Handles the ReturnBikeAtLocation command.
     * Processes bike return at a specific location based on the scenario.
     */
    @CommandHandler
    fun handle(
        command: ReturnBikeAtLocation,
        @InjectEntity state: BikeReturnState,
        eventAppender: EventAppender
    ): BikeReturnResult {
        logger.info("Handling ReturnBikeAtLocation command for rental: {}", command.rentalId)

        val event = BikeReturned(
            returnLocation = command.returnLocation,
            rentalId = command.rentalId,
            bikeId = state.getBikeId() ?: ""
        )
        eventAppender.append(event)

        return BikeReturnResult(returnConfirmed = true)
    }
}

