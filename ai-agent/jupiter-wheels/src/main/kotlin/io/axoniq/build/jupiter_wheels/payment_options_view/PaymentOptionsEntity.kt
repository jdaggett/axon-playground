package io.axoniq.build.jupiter_wheels.payment_options_view

import jakarta.persistence.*

/**
 * JPA Entity representing payment options for rentals.
 * Used by the Payment Options View component to store and query payment retry options.
 */
@Entity
@Table(name = "payment_options")
data class PaymentOptionsEntity(
    @Id
    val rentalId: String,
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "available_payment_methods",
        joinColumns = [JoinColumn(name = "rental_id")]
    )
    @Column(name = "payment_method")
    val availablePaymentMethods: MutableList<String> = mutableListOf(),
    
    @Column(name = "original_payment_method")
    val originalPaymentMethod: String
) {
    constructor() : this("", mutableListOf(), "")
}

