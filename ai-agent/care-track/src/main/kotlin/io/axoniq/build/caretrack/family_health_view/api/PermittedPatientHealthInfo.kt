package io.axoniq.build.caretrack.family_health_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PermittedPatientHealthInfo",
  namespace = "caretrack",
)
public data class PermittedPatientHealthInfo(
  public val patientId: String,
  public val familyMemberEmail: String,
)
