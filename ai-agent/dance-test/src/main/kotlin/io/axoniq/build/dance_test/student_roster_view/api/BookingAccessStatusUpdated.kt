package io.axoniq.build.dance_test.student_roster_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BookingAccessStatusUpdated",
  namespace = "dance-test",
)
public data class BookingAccessStatusUpdated(
  public val instructorId: String,
  public val newAccessStatus: String,
  public val reason: String?,
  @EventTag(key = "Student")
  public val studentId: String,
)
