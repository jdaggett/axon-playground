package io.axoniq.build.dance_test.transaction_history_view.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionCompleted",
  namespace = "dance-test",
)
public data class SessionCompleted(
  public val actualDuration: Int,
  public val completionDate: LocalDateTime,
  @EventTag(key = "Session")
  public val sessionId: String,
)
