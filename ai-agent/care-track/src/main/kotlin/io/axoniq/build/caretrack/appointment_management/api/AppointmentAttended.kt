package io.axoniq.build.caretrack.appointment_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AppointmentAttended",
  namespace = "caretrack",
)
public data class AppointmentAttended(
  public val doctorId: String,
  @EventTag(key = "Appointment")
  public val appointmentId: String,
)
