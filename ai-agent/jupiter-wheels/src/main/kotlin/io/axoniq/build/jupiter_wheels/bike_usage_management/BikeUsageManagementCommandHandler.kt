package io.axoniq.build.jupiter_wheels.bike_usage_management

import io.axoniq.build.jupiter_wheels.bike_usage_management.api.*
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
 * Command handler for the Bike Usage Management component.
 * Handles bike usage lifecycle during rentals including pause/resume operations and early rental termination.
 */
class BikeUsageManagementCommandHandler(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeUsageManagementCommandHandler::class.java)
        private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    }

    /**
     * Handles PauseRental command - pauses an active rental
     */
    @CommandHandler
    fun handle(
        command: PauseRental,
        @InjectEntity state: BikeUsageManagementState,
        eventAppender: EventAppender,
        processingContext: ProcessingContext
    ): RentalPauseResult {
        logger.info("Handling PauseRental command for rental: ${command.rentalId}")
        
        // Validate that rental can be paused
        if (state.getUsageStatus() != "IN_USE") {
            throw IllegalStateException("Rental cannot be paused - current status: ${state.getUsageStatus()}")
        }

        val pauseTime = LocalDateTime.now()
        val event = RentalPaused(
            pauseStartTime = pauseTime,
            rentalId = command.rentalId
        )

        eventAppender.append(event)
        logger.info("Rental paused for rental: ${command.rentalId}")

        // Set deadline for pause timeout (simulated)
        executor.submit {
            try {
                Thread.sleep(Duration.ofMinutes(30).toMillis()) // 30 minute pause limit
                val timeoutDeadline = RentalPauseTimeout(rentalId = command.rentalId)
                commandGateway.send(timeoutDeadline, processingContext)
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
            }
        }

        return RentalPauseResult(pauseConfirmed = true)
    }

    /**
     * Handles EndRentalEarlyDueToProblem command - ends rental early due to reported problems
     */
    @CommandHandler
    fun handle(
        command: EndRentalEarlyDueToProblem,
        @InjectEntity state: BikeUsageManagementState,
        eventAppender: EventAppender
    ): EarlyRentalEndResult {
        logger.info("Handling EndRentalEarlyDueToProblem command for rental: ${command.rentalId}")

        // Validate that rental is active
        val currentStatus = state.getUsageStatus()
        if (currentStatus != "IN_USE" && currentStatus != "PAUSED") {
            throw IllegalStateException("Rental cannot be ended early - current status: $currentStatus")
        }

        val event = RentalEndedEarly(
            problemDescription = command.problemDescription,
            rentalId = command.rentalId
        )

        eventAppender.append(event)
        logger.info("Rental ended early for rental: ${command.rentalId} due to: ${command.problemDescription}")

        return EarlyRentalEndResult(rentalEnded = true)
    }

    /**
     * Handles ResumeRental command - resumes a paused rental
     */
    @CommandHandler
    fun handle(
        command: ResumeRental,
        @InjectEntity state: BikeUsageManagementState,
        eventAppender: EventAppender
    ): RentalResumeResult {
        logger.info("Handling ResumeRental command for rental: ${command.rentalId}")

        // Validate that rental is paused
        if (state.getUsageStatus() != "PAUSED") {
            throw IllegalStateException("Rental cannot be resumed - current status: ${state.getUsageStatus()}")
        }

        val pauseStartTime = state.getPauseStartTime()
            ?: throw IllegalStateException("No pause start time found for rental")

        val pauseDuration = Duration.between(pauseStartTime, LocalDateTime.now()).toMinutes().toInt()
        
        val event = RentalResumed(
            pauseDuration = pauseDuration,
            rentalId = command.rentalId
        )

        eventAppender.append(event)
        logger.info("Rental resumed for rental: ${command.rentalId} after ${pauseDuration} minutes")

        return RentalResumeResult(resumeConfirmed = true)
    }

    /**
     * Handles RentalPauseTimeout deadline - charges extra fees for extended pause
     */
    @CommandHandler
    fun handle(
        deadline: RentalPauseTimeout,
        @InjectEntity state: BikeUsageManagementState,
        eventAppender: EventAppender
    ) {
        logger.info("Handling RentalPauseTimeout deadline for rental: ${deadline.rentalId}")

        // Only apply fees if rental is still paused
        if (state.getUsageStatus() == "PAUSED") {
            val extraFee = 5.0 // $5 fee for extended pause
            val event = ExtraFeesChargedExtendedPause(
                extraFee = extraFee,
                rentalId = deadline.rentalId
            )
            
            eventAppender.append(event)
            logger.info("Extra fees of $extraFee charged for extended pause on rental: ${deadline.rentalId}")
        }
    }
}

