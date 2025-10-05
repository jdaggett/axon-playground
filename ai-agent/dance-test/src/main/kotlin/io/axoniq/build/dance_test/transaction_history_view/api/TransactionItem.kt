package io.axoniq.build.dance_test.transaction_history_view.api

import java.time.LocalDate
import kotlin.Double
import kotlin.String

public data class TransactionItem(
  public val amount: Double,
  public val description: String,
  public val transactionDate: LocalDate,
  public val transactionType: String,
  public val transactionId: String,
)
