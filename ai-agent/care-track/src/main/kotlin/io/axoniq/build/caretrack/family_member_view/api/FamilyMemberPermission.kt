package io.axoniq.build.caretrack.family_member_view.api

import kotlin.String

public data class FamilyMemberPermission(
  public val familyMemberEmail: String,
  public val accessLevel: String,
)
