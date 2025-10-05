package io.axoniq.build.caretrack.notification_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateTreatmentNotification",
  namespace = "caretrack",
)
public data class CreateTreatmentNotification(
  @TargetEntityId
  public val patientId: String,
  public val treatmentDetails: String,
)
