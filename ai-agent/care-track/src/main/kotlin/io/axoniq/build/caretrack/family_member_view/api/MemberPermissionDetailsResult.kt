package io.axoniq.build.caretrack.family_member_view.api

import kotlin.Boolean
import kotlin.String

public data class MemberPermissionDetailsResult(
  public val familyMemberEmail: String,
  public val canViewTreatments: Boolean,
  public val canViewDiagnoses: Boolean,
  public val canViewAppointments: Boolean,
  public val accessLevel: String,
)
