package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.Double
import kotlin.Int
import kotlin.collections.List

public data class MonthlyRevenueData(
  public val revenueByPaymentMethod: List<PaymentMethodRevenue>,
  public val totalRevenue: Double,
  public val totalTransactions: Int,
  public val averageTransactionValue: Double,
)
