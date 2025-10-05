package io.axoniq.build.dance_test.session_management.api

import kotlin.Boolean
import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "SessionCompletedWithFullCharge",
  namespace = "dance-test",
)
public data class SessionCompletedWithFullCharge(
  public val actualDuration: Int,
  public val fullChargeApplied: Boolean,
  @EventTag(key = "Session")
  public val sessionId: String,
)
