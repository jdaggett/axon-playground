package io.axoniq.build.caretrack.notification_management.api

import kotlin.Boolean
import kotlin.String

public data class TreatmentNotificationResult(
  public val notificationId: String,
  public val notificationCreated: Boolean,
)
