package io.axoniq.build.caretrack.notification_management.api

import kotlin.Boolean
import kotlin.String

public data class MissedAppointmentAlertResult(
  public val alertId: String,
  public val alertCreated: Boolean,
)
