package io.axoniq.build.dance_test.transaction_history_view.api

import kotlin.Int
import kotlin.collections.List

public data class StudentSessionHistoryData(
  public val totalSessions: Int,
  public val sessions: List<SessionHistoryItem>,
)
