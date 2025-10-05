package io.axoniq.build.caretrack.family_member_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "FamilyMemberList",
  namespace = "caretrack",
)
public data class FamilyMemberList(
  public val patientId: String,
)
