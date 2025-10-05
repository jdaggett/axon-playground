package io.axoniq.build.caretrack.family_member_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "FamilyMemberPermissions",
  namespace = "caretrack",
)
public data class FamilyMemberPermissions(
  public val patientId: String,
)
