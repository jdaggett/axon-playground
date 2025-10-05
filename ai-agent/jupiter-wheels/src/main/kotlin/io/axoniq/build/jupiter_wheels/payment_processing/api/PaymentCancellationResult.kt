package io.axoniq.build.jupiter_wheels.payment_processing.api

import kotlin.Boolean

public data class PaymentCancellationResult(
  public val cancellationConfirmed: Boolean,
)
