package io.axoniq.build.caretrack.appointment_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AppointmentCancelled",
  namespace = "caretrack",
)
public data class AppointmentCancelled(
  public val doctorId: String,
  public val cancellationReason: String?,
  @EventTag(key = "Appointment")
  public val appointmentId: String,
)
