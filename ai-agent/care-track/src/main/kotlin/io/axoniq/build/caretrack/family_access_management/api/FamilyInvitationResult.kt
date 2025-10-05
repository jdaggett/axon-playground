package io.axoniq.build.caretrack.family_access_management.api

import kotlin.Boolean
import kotlin.String

public data class FamilyInvitationResult(
  public val invitationId: String,
  public val invitationSent: Boolean,
)
