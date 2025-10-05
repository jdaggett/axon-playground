package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String

public data class PaymentDetailsResult(
  public val paymentId: String,
  public val redirectUrl: String,
)
