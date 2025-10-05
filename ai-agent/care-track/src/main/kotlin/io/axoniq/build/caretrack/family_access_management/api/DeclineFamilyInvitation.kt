package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DeclineFamilyInvitation",
  namespace = "caretrack",
)
public data class DeclineFamilyInvitation(
  public val familyMemberEmail: String,
  @TargetEntityId
  public val invitationId: String,
)
