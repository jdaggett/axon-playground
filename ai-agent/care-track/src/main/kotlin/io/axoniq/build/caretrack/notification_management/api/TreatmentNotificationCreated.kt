package io.axoniq.build.caretrack.notification_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TreatmentNotificationCreated",
  namespace = "caretrack",
)
public data class TreatmentNotificationCreated(
  public val patientId: String,
  @EventTag(key = "Notification")
  public val notificationId: String,
  public val treatmentDetails: String,
)
