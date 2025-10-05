package io.axoniq.build.dance_test.transaction_history_view.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionCancelled",
  namespace = "dance-test",
)
public data class SessionCancelled(
  public val cancellationTime: LocalDateTime,
  @EventTag(key = "Session")
  public val sessionId: String,
)
