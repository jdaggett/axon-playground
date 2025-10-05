package io.axoniq.build.caretrack.appointment_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "MarkAppointmentAttended",
  namespace = "caretrack",
)
public data class MarkAppointmentAttended(
  public val doctorId: String,
  @TargetEntityId
  public val appointmentId: String,
)
