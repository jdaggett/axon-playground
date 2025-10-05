package io.axoniq.build.caretrack.notification_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "UrgentHealthNotificationCreated",
  namespace = "caretrack",
)
public data class UrgentHealthNotificationCreated(
  public val patientId: String,
  public val message: String,
  @EventTag(key = "Notification")
  public val notificationId: String,
  public val priority: String,
)
