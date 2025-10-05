package io.axoniq.build.dance_test.transaction_history_view.api

import kotlin.Int
import kotlin.collections.List

public data class TransactionHistoryData(
  public val transactions: List<TransactionItem>,
  public val totalTransactions: Int,
)
