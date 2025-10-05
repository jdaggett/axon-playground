package io.axoniq.build.jupiter_wheels.payment_processing

import io.axoniq.build.jupiter_wheels.payment_processing.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * Payment Processing Controller - REST API for Payment Processing component.
 * Exposes endpoints for payment processing operations including success confirmation,
 * failure reporting, payment details retrieval, retry attempts, and cancellation.
 */
@RestController
@RequestMapping("/api/payment-processing")
class PaymentProcessingController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PaymentProcessingController::class.java)
    }

    /**
     * Endpoint to report payment failure.
     */
    @PostMapping("/report-failure")
    fun reportPaymentFailure(@RequestBody command: ReportPaymentFailure): ResponseEntity<String> {
        logger.info("Dispatching ReportPaymentFailure command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment failure reported successfully")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ReportPaymentFailure command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to report payment failure")
        }
    }

    /**
     * Endpoint to confirm payment success.
     */
    @PostMapping("/confirm-success")
    fun confirmPaymentSuccess(@RequestBody command: ConfirmPaymentSuccess): ResponseEntity<String> {
        logger.info("Dispatching ConfirmPaymentSuccess command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment success confirmed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ConfirmPaymentSuccess command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm payment success")
        }
    }

    /**
     * Endpoint to return payment details.
     */
    @PostMapping("/payment-details")
    fun returnPaymentDetails(@RequestBody command: ReturnPaymentDetails): ResponseEntity<String> {
        logger.info("Dispatching ReturnPaymentDetails command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment details prepared")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ReturnPaymentDetails command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to prepare payment details")
        }
    }

    /**
     * Endpoint to retry payment.
     */
    @PostMapping("/retry")
    fun retryPayment(@RequestBody command: RetryPayment): ResponseEntity<String> {
        logger.info("Dispatching RetryPayment command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment retry processed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RetryPayment command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to retry payment")
        }
    }

    /**
     * Endpoint to confirm payment cancellation.
     */
    @PostMapping("/confirm-cancellation")
    fun confirmPaymentCancellation(@RequestBody command: ConfirmPaymentCancellation): ResponseEntity<String> {
        logger.info("Dispatching ConfirmPaymentCancellation command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Payment cancellation confirmed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ConfirmPaymentCancellation command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to confirm payment cancellation")
        }
    }
}

