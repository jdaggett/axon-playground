package io.axoniq.build.caretrack.family_member_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "MemberPermissionDetails",
  namespace = "caretrack",
)
public data class MemberPermissionDetails(
  public val patientId: String,
  public val familyMemberEmail: String,
)
