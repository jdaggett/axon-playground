package io.axoniq.build.jupiter_wheels.payment_options_view

import io.axoniq.build.jupiter_wheels.payment_options_view.api.PaymentRetryOptions
import io.axoniq.build.jupiter_wheels.payment_options_view.api.PaymentRetryOptionsResult
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for exposing payment options queries.
 * Part of the Payment Options View component that handles payment retry and options queries.
 */
@RestController
@RequestMapping("/api/payment-options")
class PaymentOptionsViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PaymentOptionsViewController::class.java)
    }

    /**
     * REST endpoint to get payment retry options for a specific rental.
     * Returns available payment methods and original payment method for payment retry.
     */
    @GetMapping("/{rentalId}/retry-options")
    fun getPaymentRetryOptions(@PathVariable rentalId: String): CompletableFuture<PaymentRetryOptionsResult> {
        logger.info("REST request for payment retry options for rental ID: $rentalId")

        val query = PaymentRetryOptions(rentalId)
        return queryGateway.query(query, PaymentRetryOptionsResult::class.java, null)
    }
}