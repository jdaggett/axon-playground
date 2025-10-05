package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FamilyMemberAccessRevoked",
  namespace = "caretrack",
)
public data class FamilyMemberAccessRevoked(
  public val familyMemberEmail: String,
  @EventTag(key = "Patient")
  public val patientId: String,
)
