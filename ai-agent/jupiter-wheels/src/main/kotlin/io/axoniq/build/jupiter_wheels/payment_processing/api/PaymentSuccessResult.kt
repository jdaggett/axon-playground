package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String

public data class PaymentSuccessResult(
  public val paymentStatus: String,
)
