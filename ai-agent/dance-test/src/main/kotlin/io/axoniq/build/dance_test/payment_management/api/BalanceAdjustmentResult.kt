package io.axoniq.build.dance_test.payment_management.api

import kotlin.Boolean
import kotlin.Double

public data class BalanceAdjustmentResult(
  public val success: Boolean,
  public val newBalance: Double,
)
