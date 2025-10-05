package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ChangeFamilyMemberPermissions",
  namespace = "caretrack",
)
public data class ChangeFamilyMemberPermissions(
  public val familyMemberEmail: String,
  public val newAccessLevel: String,
  @TargetEntityId
  public val patientId: String,
)
