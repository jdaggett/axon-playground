package io.axoniq.build.dance_test.booking_access_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BookingAccessBlocked",
  namespace = "dance-test",
)
public data class BookingAccessBlocked(
  public val instructorId: String,
  public val blockingReason: String,
  @EventTag(key = "Student")
  public val studentId: String,
)
