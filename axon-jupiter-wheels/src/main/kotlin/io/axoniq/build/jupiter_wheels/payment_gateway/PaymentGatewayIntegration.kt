package io.axoniq.build.jupiter_wheels.payment_gateway

import io.axoniq.build.jupiter_wheels.payment_gateway.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Payment Gateway Integration - External System Component
 *
 * This component handles payment processing with external payment provider.
 * It receives events from the system and performs external payment operations,
 * then sends commands back to the system based on the payment results.
 */
@Service
class PaymentGatewayIntegration(
    private val commandGateway: CommandGateway
) {
    
    private val logger: Logger = LoggerFactory.getLogger(PaymentGatewayIntegration::class.java)

    /**
     * Handles BikeRentalRequested event to prepare payment with external payment provider.
     * 
     * When a bike rental is requested, this handler initiates the payment preparation process
     * with the external payment gateway and returns payment details to the system.
     *
     * @param event BikeRentalRequested event containing rental details
     * @param processingContext The processing context for command sending
     */
    @EventHandler
    fun preparePayment(event: BikeRentalRequested, processingContext: ProcessingContext) {
        logger.info("Preparing payment for bike rental - RentalId: ${event.rentalId}, BikeId: ${event.bikeId}, UserId: ${event.userId}")

        // Simulate external payment gateway preparation
        // In real implementation, this would call external payment API
        logger.debug("Calling external payment gateway to prepare payment for rental: ${event.rentalId}")

        // Generate payment ID (in real implementation, this would come from external gateway)
        val paymentId = "PAY_${event.rentalId}_${System.currentTimeMillis()}"

        // Return payment details to the system
        val returnPaymentCommand = ReturnPaymentDetails(
            paymentId = paymentId,
            rentalId = event.rentalId
        )

        logger.info("Returning payment details to system - PaymentId: $paymentId, RentalId: ${event.rentalId}")
        commandGateway.send(returnPaymentCommand, processingContext)
    }

    /**
     * Handles RetryPayment event to process payment retry with external payment provider.
     * 
     * When a payment retry is requested, this handler attempts to process the payment again
     * with the external payment gateway and either confirms success or reports failure.
     * 
     * @param event RetryPayment event containing retry details
     * @param processingContext The processing context for command sending
     */
    @EventHandler
    fun processPaymentRetry(event: RetryPayment, processingContext: ProcessingContext) {
        logger.info("Processing payment retry for rental - RentalId: ${event.rentalId}, PaymentMethod: ${event.paymentMethod}")

        // Simulate external payment processing
        // In real implementation, this would call external payment API with retry logic
        logger.debug("Attempting payment retry with external gateway for rental: ${event.rentalId}")

        // Generate payment ID for retry attempt
        val paymentId = "PAY_RETRY_${event.rentalId}_${System.currentTimeMillis()}"

        try {
            // Simulate payment processing result (random for demonstration)
            val paymentSuccess = System.currentTimeMillis() % 2 == 0L

            if (paymentSuccess) {
                logger.info("Payment retry successful - PaymentId: $paymentId, RentalId: ${event.rentalId}")
                
                val confirmCommand = ConfirmPaymentSuccess(
                    paymentId = paymentId,
                    rentalId = event.rentalId
                )
                commandGateway.send(confirmCommand, processingContext)
                
            } else {
                logger.warn("Payment retry failed - PaymentId: $paymentId, RentalId: ${event.rentalId}")

                val failureCommand = ReportPaymentFailure(
                    failureReason = "Payment declined by external gateway",
                    paymentId = paymentId,
                    rentalId = event.rentalId
                )
                commandGateway.send(failureCommand, processingContext)
            }

        } catch (exception: Exception) {
            logger.error("Exception during payment retry processing for rental: ${event.rentalId}", exception)

            val failureCommand = ReportPaymentFailure(
                failureReason = "Technical error: ${exception.message}",
                paymentId = paymentId,
                rentalId = event.rentalId
            )
            commandGateway.send(failureCommand, processingContext)
        }
    }
}