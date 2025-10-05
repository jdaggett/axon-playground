package io.axoniq.build.dance_test.session_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionNotesRecorded",
  namespace = "dance-test",
)
public data class SessionNotesRecorded(
  public val notes: String,
  @EventTag(key = "Session")
  public val sessionId: String,
)
