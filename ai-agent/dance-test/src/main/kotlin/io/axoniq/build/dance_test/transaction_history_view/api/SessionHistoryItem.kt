package io.axoniq.build.dance_test.transaction_history_view.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String

public data class SessionHistoryItem(
  public val duration: Int,
  public val sessionDate: LocalDateTime,
  public val notes: String?,
  public val status: String,
  public val sessionId: String,
  public val actualDuration: Int?,
)
