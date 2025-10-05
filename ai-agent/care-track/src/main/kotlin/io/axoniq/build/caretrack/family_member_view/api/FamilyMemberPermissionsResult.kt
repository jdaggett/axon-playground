package io.axoniq.build.caretrack.family_member_view.api

import kotlin.collections.List

public data class FamilyMemberPermissionsResult(
  public val permissions: List<FamilyMemberPermission>,
)
