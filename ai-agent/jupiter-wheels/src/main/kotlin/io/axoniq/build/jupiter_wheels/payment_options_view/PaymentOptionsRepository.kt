package io.axoniq.build.jupiter_wheels.payment_options_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for PaymentOptionsEntity.
 * Provides data access operations for the Payment Options View component.
 */
@Repository
interface PaymentOptionsRepository : JpaRepository<PaymentOptionsEntity, String>

