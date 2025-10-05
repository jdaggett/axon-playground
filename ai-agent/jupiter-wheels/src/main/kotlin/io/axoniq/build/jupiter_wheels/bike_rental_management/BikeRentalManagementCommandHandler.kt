package io.axoniq.build.jupiter_wheels.bike_rental_management

import io.axoniq.build.jupiter_wheels.bike_rental_management.api.*
import io.axoniq.build.jupiter_wheels.bike_rental_management.exception.BikeAlreadyReserved
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.Duration
import java.util.UUID
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Command handler for the Bike Rental Management component
 * Handles bike rental request validation and processing
 */
class BikeRentalManagementCommandHandler(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(BikeRentalManagementCommandHandler::class.java)
        private val executor: ExecutorService = Executors.newVirtualThreadPerTaskExecutor()
    }

    /**
     * Handles RequestBikeRental command
     * Validates bike availability and creates rental request
     */
    @CommandHandler
    fun handle(
        command: RequestBikeRental,
        @InjectEntity state: BikeRentalManagementState,
        eventAppender: EventAppender,
        processingContext: ProcessingContext
    ): BikeRentalRequestResult {
        logger.info("Handling RequestBikeRental command for userId: ${command.userId}, bikeId: ${command.bikeId}")

        // Check if bike is removed from fleet
        if (state.getBikeRemoved()) {
            throw IllegalStateException("Bike ${command.bikeId} has been removed from the fleet")
        }

        // Check if bike is not available
        if (!state.getBikeAvailable()) {
            throw IllegalStateException("Bike ${command.bikeId} is not available")
        }

        // Check if bike is already reserved
        if (state.getRentalRequestActive()) {
            throw BikeAlreadyReserved("Bike ${command.bikeId} is already reserved")
        }

        // Generate rental ID and create event
        val rentalId = UUID.randomUUID().toString()
        val event = BikeRentalRequested(
            userId = command.userId,
            rentalId = rentalId,
            bikeId = command.bikeId
        )

        eventAppender.append(event)

        // Schedule payment timeout deadline
        executor.submit {
            try {
                Thread.sleep(Duration.ofMinutes(10).toMillis())
                val timeoutDeadline = PaymentTimeout(rentalId = rentalId)
                commandGateway.send(timeoutDeadline, processingContext)
                logger.info("Payment timeout deadline scheduled for rentalId: $rentalId")
            } catch (e: InterruptedException) {
                Thread.currentThread().interrupt()
                logger.warn("Payment timeout scheduling interrupted for rentalId: $rentalId")
            }
        }

        logger.info("Bike rental requested successfully. RentalId: $rentalId")
        return BikeRentalRequestResult(rentalId = rentalId, bikeId = command.bikeId)
    }

    /**
     * Handles PaymentTimeout deadline
     * Rejects rental request due to payment timeout
     */
    @CommandHandler
    fun handle(
        deadline: PaymentTimeout,
        @InjectEntity state: BikeRentalManagementState,
        eventAppender: EventAppender
    ) {
        logger.info("Handling PaymentTimeout deadline for rentalId: ${deadline.rentalId}")

        // Only process if rental request is still active
        if (state.getRentalRequestActive() && state.getRentalId() == deadline.rentalId) {
            val event = RentalRequestRejectedTimeout(
                rentalId = deadline.rentalId,
                bikeId = state.getBikeId()
            )
            eventAppender.append(event)
            logger.info("Rental request rejected due to timeout. RentalId: ${deadline.rentalId}")
        } else {
            logger.info("Payment timeout ignored - rental request no longer active for rentalId: ${deadline.rentalId}")
        }
    }
}

