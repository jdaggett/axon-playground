package io.axoniq.build.caretrack.notification_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "MissedAppointmentAlertCreated",
  namespace = "caretrack",
)
public data class MissedAppointmentAlertCreated(
  public val patientId: String,
  @EventTag(key = "Notification")
  public val alertId: String,
  public val appointmentId: String,
  public val alertMessage: String,
)
