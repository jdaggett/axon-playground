package io.axoniq.build.jupiter_wheels.rental_extension_management

import io.axoniq.build.jupiter_wheels.rental_extension_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.time.LocalDateTime
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Command handler for Rental Extension Management component.
 * Handles rental time extension requests and processing.
 */
class RentalExtensionManagementCommandHandler(
    private val commandGateway: CommandGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(RentalExtensionManagementCommandHandler::class.java)
        private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    }

    /**
     * Handles RequestRentalExtension command - processes rental extension requests
     * Given that a bike is marked as in use, when the user requests rental extension,
     * then the rental extension is requested
     */
    @CommandHandler
    fun handle(
        command: RequestRentalExtension,
        @InjectEntity state: RentalExtensionManagementState,
        eventAppender: EventAppender,
        processingContext: ProcessingContext
    ): RentalExtensionRequestResult {
        logger.info("Processing rental extension request for rental ID: ${command.rentalId}")
        
        val event = RentalExtensionRequested(
            additionalTime = command.additionalTime,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        // Set timeout deadline for extension processing
        executor.submit {
            try {
                Thread.sleep(Duration.ofMinutes(30).toMillis()) // 30 minute timeout
                val timeoutDeadline = RentalExtensionProcessingTimeout(rentalId = command.rentalId)
                commandGateway.send(timeoutDeadline, processingContext)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.warn("Extension processing timeout interrupted for rental: ${command.rentalId}")
            }
        }
        
        return RentalExtensionRequestResult(
            extensionRequestId = "ext-${command.rentalId}-${System.currentTimeMillis()}"
        )
    }

    /**
     * Handles ApproveRentalExtension command - approves rental extension requests
     * Given that a rental extension is requested, when the system approves rental extension,
     * then the rental extension is approved
     */
    @CommandHandler
    fun handle(
        command: ApproveRentalExtension,
        @InjectEntity state: RentalExtensionManagementState,
        eventAppender: EventAppender
    ): RentalExtensionApprovalResult {
        logger.info("Processing rental extension approval for rental ID: ${command.rentalId}")

        if (!state.getExtensionRequested()) {
            logger.error("Cannot approve extension - no extension request found for rental: ${command.rentalId}")
            return RentalExtensionApprovalResult(approvalConfirmed = false)
        }

        val newEndTime = LocalDateTime.now().plusMinutes(command.approvedTime.toLong())

        val event = RentalExtensionApproved(
            approvedTime = command.approvedTime,
            newEndTime = newEndTime,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        return RentalExtensionApprovalResult(approvalConfirmed = true)
    }

    /**
     * Handles RentalExtensionProcessingTimeout deadline - activates grace period when extension processing times out
     */
    @CommandHandler
    fun handle(
        deadline: RentalExtensionProcessingTimeout,
        @InjectEntity state: RentalExtensionManagementState,
        eventAppender: EventAppender
    ) {
        logger.info("Processing rental extension timeout for rental ID: ${deadline.rentalId}")

        if (state.getExtensionRequested() && !state.getGracePeriodActive()) {
            val event = GracePeriodActivated(
                gracePeriodMinutes = 15, // 15 minute grace period
                rentalId = deadline.rentalId
            )
            eventAppender.append(event)
            logger.info("Grace period activated for rental: ${deadline.rentalId}")
        }
    }
}

