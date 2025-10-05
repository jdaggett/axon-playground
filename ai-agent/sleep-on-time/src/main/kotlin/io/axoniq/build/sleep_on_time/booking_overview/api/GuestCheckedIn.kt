package io.axoniq.build.sleep_on_time.booking_overview.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "GuestCheckedIn",
  namespace = "sleep-on-time",
)
public data class GuestCheckedIn(
  public val checkedInAt: LocalDateTime,
  @EventTag(key = "Booking")
  public val bookingId: String,
  @EventTag(key = "Guest")
  public val guestId: String,
  @EventTag(key = "Container")
  public val containerId: String,
)
