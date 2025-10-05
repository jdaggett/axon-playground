package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RemoveFamilyMemberAccess",
  namespace = "caretrack",
)
public data class RemoveFamilyMemberAccess(
  public val familyMemberEmail: String,
  @TargetEntityId
  public val patientId: String,
)
