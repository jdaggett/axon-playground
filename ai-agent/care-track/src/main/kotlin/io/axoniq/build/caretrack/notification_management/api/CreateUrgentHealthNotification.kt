package io.axoniq.build.caretrack.notification_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateUrgentHealthNotification",
  namespace = "caretrack",
)
public data class CreateUrgentHealthNotification(
  public val message: String,
  @TargetEntityId
  public val patientId: String,
  public val priority: String,
)
