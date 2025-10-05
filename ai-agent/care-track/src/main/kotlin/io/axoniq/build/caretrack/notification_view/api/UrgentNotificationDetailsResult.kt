package io.axoniq.build.caretrack.notification_view.api

import java.time.LocalDateTime
import kotlin.Boolean
import kotlin.String

public data class UrgentNotificationDetailsResult(
  public val notificationId: String,
  public val acknowledged: Boolean,
  public val message: String,
  public val patientName: String,
  public val createdDate: LocalDateTime,
  public val priority: String,
)
