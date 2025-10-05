package io.axoniq.build.jupiter_wheels.payment_options_view

import io.axoniq.build.jupiter_wheels.payment_options_view.api.PaymentRetryOptions
import io.axoniq.build.jupiter_wheels.payment_options_view.api.PaymentRetryOptionsResult
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * View component for handling payment options queries.
 * Part of the Payment Options View component that handles payment retry and options queries.
 */
@Component
class PaymentOptionsViewComponent(
    private val paymentOptionsRepository: PaymentOptionsRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PaymentOptionsViewComponent::class.java)
    }

    /**
     * Handles PaymentRetryOptions query to retrieve available payment methods for a rental.
     * Returns payment retry options including available payment methods and original payment method.
     */
    @QueryHandler
    fun handle(query: PaymentRetryOptions): PaymentRetryOptionsResult {
        logger.info("Handling PaymentRetryOptions query for rental ID: ${query.rentalId}")

        val paymentOptions = paymentOptionsRepository.findById(query.rentalId)
            .orElseThrow { IllegalArgumentException("Payment options not found for rental ID: ${query.rentalId}") }

        return PaymentRetryOptionsResult(
            availablePaymentMethods = paymentOptions.availablePaymentMethods.toList(),
            rentalId = paymentOptions.rentalId,
            originalPaymentMethod = paymentOptions.originalPaymentMethod
        )
    }
}