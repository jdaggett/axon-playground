package io.axoniq.build.caretrack.notification_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "UrgentNotificationDetails",
  namespace = "caretrack",
)
public data class UrgentNotificationDetails(
  public val notificationId: String,
)
