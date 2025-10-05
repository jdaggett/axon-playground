package io.axoniq.build.caretrack.notification_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "UrgentNotificationAcknowledged",
  namespace = "caretrack",
)
public data class UrgentNotificationAcknowledged(
  public val familyMemberEmail: String,
  @EventTag(key = "Notification")
  public val notificationId: String,
)
