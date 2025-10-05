package io.axoniq.build.sleep_on_time.notification_service.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "ContainerIssueReported",
  namespace = "sleep-on-time",
)
public data class ContainerIssueReported(
  @EventTag(key = "Issue")
  public val issueId: String,
  @EventTag(key = "Booking")
  public val bookingId: String,
  public val issueType: String,
  @EventTag(key = "Guest")
  public val guestId: String,
  public val description: String,
  public val reportedAt: LocalDateTime,
  public val severity: String,
  @EventTag(key = "Container")
  public val containerId: String,
)
