package io.axoniq.build.caretrack.notification_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "AcknowledgeUrgentNotification",
  namespace = "caretrack",
)
public data class AcknowledgeUrgentNotification(
  public val familyMemberEmail: String,
  @TargetEntityId
  public val notificationId: String,
)
