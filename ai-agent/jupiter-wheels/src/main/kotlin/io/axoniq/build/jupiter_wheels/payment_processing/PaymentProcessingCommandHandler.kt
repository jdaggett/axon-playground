package io.axoniq.build.jupiter_wheels.payment_processing

import io.axoniq.build.jupiter_wheels.payment_processing.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.modelling.annotations.InjectEntity
import org.axonframework.eventhandling.gateway.EventAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.UUID

/**
 * Payment Processing Command Handler - Handles commands for the Payment Processing component.
 * This handler processes payment-related commands including success confirmation, failure reporting,
 * payment details return, retry attempts, and cancellation confirmation.
 */
class PaymentProcessingCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PaymentProcessingCommandHandler::class.java)
    }

    /**
     * Handles ReportPaymentFailure command.
     * Reports payment failure and determines if retry is allowed based on current state.
     */
    @CommandHandler
    fun handle(
        command: ReportPaymentFailure,
        @InjectEntity state: PaymentProcessingState,
        eventAppender: EventAppender
    ): PaymentFailureResult {
        logger.info("Processing ReportPaymentFailure command for rentalId: ${command.rentalId}, paymentId: ${command.paymentId}")

        // Validate that payment is in a state where failure can be reported
        val currentStatus = state.getPaymentStatus()
        require(currentStatus == "PREPARED") { 
            "Payment failure can only be reported for prepared payments, current status: $currentStatus"
        }

        // Append payment failed event
        val event = PaymentFailed(
            failureReason = command.failureReason,
            paymentId = command.paymentId,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        // Determine if retry is allowed (allow retry for temporary failures)
        val retryAllowed = !command.failureReason.contains("PERMANENT", ignoreCase = true)
        
        logger.info("Payment failure reported for rentalId: ${command.rentalId}, retry allowed: $retryAllowed")
        return PaymentFailureResult(retryAllowed = retryAllowed)
    }

    /**
     * Handles ConfirmPaymentSuccess command.
     * Confirms successful payment completion.
     */
    @CommandHandler
    fun handle(
        command: ConfirmPaymentSuccess,
        @InjectEntity state: PaymentProcessingState,
        eventAppender: EventAppender
    ): PaymentSuccessResult {
        logger.info("Processing ConfirmPaymentSuccess command for rentalId: ${command.rentalId}, paymentId: ${command.paymentId}")

        // Validate that payment is in a state where success can be confirmed
        val currentStatus = state.getPaymentStatus()
        require(currentStatus == "PREPARED") { 
            "Payment success can only be confirmed for prepared payments, current status: $currentStatus" 
        }

        // Append payment completed event
        val event = PaymentCompleted(
            paymentId = command.paymentId,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        logger.info("Payment success confirmed for rentalId: ${command.rentalId}")
        return PaymentSuccessResult(paymentStatus = "COMPLETED")
    }

    /**
     * Handles ReturnPaymentDetails command.
     * Prepares payment and returns payment details including redirect URL.
     */
    @CommandHandler
    fun handle(
        command: ReturnPaymentDetails,
        @InjectEntity state: PaymentProcessingState,
        eventAppender: EventAppender
    ): PaymentDetailsResult {
        logger.info("Processing ReturnPaymentDetails command for rentalId: ${command.rentalId}")

        // Generate new payment ID if not already set
        val paymentId = command.paymentId
        
        // Append payment prepared event
        val event = PaymentPrepared(
            paymentId = paymentId,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        // Generate redirect URL for payment processing
        val redirectUrl = "https://payment-gateway.jupiter-wheels.com/pay/${paymentId}"
        
        logger.info("Payment details prepared for rentalId: ${command.rentalId}, paymentId: $paymentId")
        return PaymentDetailsResult(
            paymentId = paymentId,
            redirectUrl = redirectUrl
        )
    }
    
    /**
     * Handles RetryPayment command.
     * Attempts to retry payment with a new payment method.
     */
    @CommandHandler
    fun handle(
        command: RetryPayment,
        @InjectEntity state: PaymentProcessingState,
        eventAppender: EventAppender
    ): PaymentRetryResult {
        logger.info("Processing RetryPayment command for rentalId: ${command.rentalId}")

        // Validate that payment is in a failed state
        val currentStatus = state.getPaymentStatus()
        require(currentStatus == "FAILED") { 
            "Payment retry can only be attempted for failed payments, current status: $currentStatus" 
        }

        // Simulate payment retry logic
        val retrySuccess = !command.paymentMethod.equals("INVALID_METHOD", ignoreCase = true)

        if (retrySuccess) {
            // Generate new payment ID for retry
            val newPaymentId = UUID.randomUUID().toString()

            // Append payment completed event for successful retry
            val event = PaymentCompleted(
                paymentId = newPaymentId,
                rentalId = command.rentalId
            )
            eventAppender.append(event)
            
            logger.info("Payment retry successful for rentalId: ${command.rentalId}")
            return PaymentRetryResult(
                redirectUrl = null,
                paymentId = newPaymentId
            )
        } else {
            // Append payment failed event for unsuccessful retry
            val event = PaymentFailed(
                failureReason = "Invalid payment method: ${command.paymentMethod}",
                paymentId = state.getPaymentId() ?: UUID.randomUUID().toString(),
                rentalId = command.rentalId
            )
            eventAppender.append(event)

            logger.info("Payment retry failed for rentalId: ${command.rentalId}")
            return PaymentRetryResult(
                redirectUrl = null,
                paymentId = null
            )
        }
    }

    /**
     * Handles ConfirmPaymentCancellation command.
     * Confirms payment cancellation.
     */
    @CommandHandler
    fun handle(
        command: ConfirmPaymentCancellation,
        @InjectEntity state: PaymentProcessingState,
        eventAppender: EventAppender
    ): PaymentCancellationResult {
        logger.info("Processing ConfirmPaymentCancellation command for rentalId: ${command.rentalId}")

        // Validate that payment is in a state where cancellation can be confirmed
        val currentStatus = state.getPaymentStatus()
        require(currentStatus == "PREPARED") { 
            "Payment cancellation can only be confirmed for prepared payments, current status: $currentStatus" 
        }

        // Append payment cancelled event
        val event = PaymentCancelled(
            paymentId = command.paymentId,
            rentalId = command.rentalId
        )
        eventAppender.append(event)

        logger.info("Payment cancellation confirmed for rentalId: ${command.rentalId}")
        return PaymentCancellationResult(cancellationConfirmed = true)
    }
}

