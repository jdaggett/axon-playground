package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "InviteFamilyMember",
  namespace = "caretrack",
)
public data class InviteFamilyMember(
  public val familyMemberEmail: String,
  public val accessLevel: String,
  @TargetEntityId
  public val patientId: String,
)
