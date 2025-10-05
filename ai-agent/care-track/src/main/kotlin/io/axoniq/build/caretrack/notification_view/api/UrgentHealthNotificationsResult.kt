package io.axoniq.build.caretrack.notification_view.api

import kotlin.collections.List

public data class UrgentHealthNotificationsResult(
  public val notifications: List<NotificationSummary>,
)
