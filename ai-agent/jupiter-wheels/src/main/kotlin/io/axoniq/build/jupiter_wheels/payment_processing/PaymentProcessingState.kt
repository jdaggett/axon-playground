package io.axoniq.build.jupiter_wheels.payment_processing

import io.axoniq.build.jupiter_wheels.payment_processing.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Payment Processing State - EventSourcedEntity for the Payment Processing component.
 * This state maintains the current status and details of payment processing for bike rentals.
 * It tracks payment lifecycle from preparation through completion, failure, or cancellation.
 */
@EventSourcedEntity
class PaymentProcessingState {

    private var paymentStatus: String? = null
    private var paymentMethod: String? = null
    private var paymentId: String? = null
    private var rentalId: String? = null

    // Getters for command handlers to access state
    fun getPaymentStatus(): String? = paymentStatus
    fun getPaymentMethod(): String? = paymentMethod
    fun getPaymentId(): String? = paymentId
    fun getRentalId(): String? = rentalId

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for BikeRentalRequested event.
     * Initializes the payment processing state when a bike rental is requested.
     */
    @EventSourcingHandler
    fun evolve(event: BikeRentalRequested) {
        this.rentalId = event.rentalId
        this.paymentStatus = "PENDING"
    }

    /**
     * Event sourcing handler for PaymentPrepared event.
     * Updates the state when payment preparation is completed.
     */
    @EventSourcingHandler
    fun evolve(event: PaymentPrepared) {
        this.paymentId = event.paymentId
        this.rentalId = event.rentalId
        this.paymentStatus = "PREPARED"
    }

    /**
     * Event sourcing handler for PaymentCompleted event.
     * Updates the state when payment is successfully completed.
     */
    @EventSourcingHandler
    fun evolve(event: PaymentCompleted) {
        this.paymentId = event.paymentId
        this.paymentStatus = "COMPLETED"
    }

    /**
     * Event sourcing handler for PaymentFailed event.
     * Updates the state when payment fails.
     */
    @EventSourcingHandler
    fun evolve(event: PaymentFailed) {
        this.paymentId = event.paymentId
        this.paymentStatus = "FAILED"
    }

    /**
     * Event sourcing handler for PaymentCancelled event.
     * Updates the state when payment is cancelled.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PaymentCancelled) {
        this.paymentStatus = "CANCELLED"
    }

    companion object {
        /**
         * Event criteria builder for Payment Processing component.
         * Defines which events should be loaded to reconstruct the payment processing state.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(rentalId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Rental", rentalId))
                .andBeingOneOfTypes(
                    BikeRentalRequested::class.java.name,
                    PaymentPrepared::class.java.name,
                    PaymentCompleted::class.java.name,
                    PaymentFailed::class.java.name,
                    PaymentCancelled::class.java.name
                )
        }
    }
}