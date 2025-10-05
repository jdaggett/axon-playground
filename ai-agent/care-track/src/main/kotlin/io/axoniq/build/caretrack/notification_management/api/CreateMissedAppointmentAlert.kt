package io.axoniq.build.caretrack.notification_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateMissedAppointmentAlert",
  namespace = "caretrack",
)
public data class CreateMissedAppointmentAlert(
  @TargetEntityId
  public val patientId: String,
  public val appointmentId: String,
  public val alertMessage: String,
)
