package io.axoniq.build.caretrack.notification_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "UrgentHealthNotifications",
  namespace = "caretrack",
)
public data class UrgentHealthNotifications(
  public val patientId: String,
  public val familyMemberEmail: String,
)
