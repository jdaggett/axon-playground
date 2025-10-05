package io.axoniq.build.sleep_on_time.xesar_access_key.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ContainerObtained",
  namespace = "sleep-on-time",
)
public data class ContainerObtained(
  @EventTag(key = "Booking")
  public val bookingId: String,
  @EventTag(key = "Guest")
  public val guestId: String,
  public val timestamp: LocalDateTime,
  @EventTag(key = "Container")
  public val containerId: String,
)
