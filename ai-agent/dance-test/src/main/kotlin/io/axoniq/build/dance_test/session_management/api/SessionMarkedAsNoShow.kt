package io.axoniq.build.dance_test.session_management.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionMarkedAsNoShow",
  namespace = "dance-test",
)
public data class SessionMarkedAsNoShow(
  public val chargeStudent: Boolean,
  public val reason: String,
  @EventTag(key = "Session")
  public val sessionId: String,
)
