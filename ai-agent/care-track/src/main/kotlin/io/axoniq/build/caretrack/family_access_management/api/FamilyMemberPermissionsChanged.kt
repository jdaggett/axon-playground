package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FamilyMemberPermissionsChanged",
  namespace = "caretrack",
)
public data class FamilyMemberPermissionsChanged(
  public val familyMemberEmail: String,
  public val newAccessLevel: String,
  @EventTag(key = "Patient")
  public val patientId: String,
)
