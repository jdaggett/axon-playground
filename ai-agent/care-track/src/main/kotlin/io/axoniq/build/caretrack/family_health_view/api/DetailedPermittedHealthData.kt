package io.axoniq.build.caretrack.family_health_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "DetailedPermittedHealthData",
  namespace = "caretrack",
)
public data class DetailedPermittedHealthData(
  public val patientId: String,
  public val familyMemberEmail: String,
  public val healthArea: String,
)
