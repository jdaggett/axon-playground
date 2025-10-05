package io.axoniq.build.jupiter_wheels.payment_options_view.api

import kotlin.String
import kotlin.collections.List

public data class PaymentRetryOptionsResult(
  public val availablePaymentMethods: List<String>,
  public val rentalId: String,
  public val originalPaymentMethod: String,
)
