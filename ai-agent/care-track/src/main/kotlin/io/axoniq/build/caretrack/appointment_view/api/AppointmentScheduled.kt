package io.axoniq.build.caretrack.appointment_view.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AppointmentScheduled",
  namespace = "caretrack",
)
public data class AppointmentScheduled(
  public val patientId: String,
  public val doctorId: String,
  public val purpose: String,
  public val appointmentDate: LocalDateTime,
  @EventTag(key = "Appointment")
  public val appointmentId: String,
)
