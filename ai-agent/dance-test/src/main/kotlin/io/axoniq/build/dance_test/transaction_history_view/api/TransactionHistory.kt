package io.axoniq.build.dance_test.transaction_history_view.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "TransactionHistory",
  namespace = "dance-test",
)
public data class TransactionHistory(
  public val transactionType: String?,
  public val startDate: LocalDate?,
  public val studentId: String,
  public val endDate: LocalDate?,
)
