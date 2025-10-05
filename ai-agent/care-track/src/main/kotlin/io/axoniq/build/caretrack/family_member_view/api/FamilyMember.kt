package io.axoniq.build.caretrack.family_member_view.api

import kotlin.String

public data class FamilyMember(
  public val email: String,
  public val accessLevel: String,
  public val status: String,
)
