package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.String

public data class PaymentMethodRevenue(
  public val paymentMethod: String,
  public val amount: Double,
)
