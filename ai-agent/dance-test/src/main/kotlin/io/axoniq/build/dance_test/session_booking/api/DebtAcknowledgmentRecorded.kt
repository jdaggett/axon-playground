package io.axoniq.build.dance_test.session_booking.api

import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "DebtAcknowledgmentRecorded",
  namespace = "dance-test",
)
public data class DebtAcknowledgmentRecorded(
  public val acknowledgedAmount: Double,
  @EventTag(key = "Student")
  public val studentId: String,
  public val sessionId: String,
)
