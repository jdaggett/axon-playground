package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.String

public data class PaymentRetryResult(
  public val redirectUrl: String?,
  public val paymentId: String?,
)
