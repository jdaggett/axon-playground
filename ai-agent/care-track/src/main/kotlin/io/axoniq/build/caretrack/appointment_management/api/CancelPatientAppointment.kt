package io.axoniq.build.caretrack.appointment_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CancelPatientAppointment",
  namespace = "caretrack",
)
public data class CancelPatientAppointment(
  public val doctorId: String,
  public val cancellationReason: String?,
  @TargetEntityId
  public val appointmentId: String,
)
