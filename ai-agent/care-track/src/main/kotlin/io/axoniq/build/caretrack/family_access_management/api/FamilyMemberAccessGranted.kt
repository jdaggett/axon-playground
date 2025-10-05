package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FamilyMemberAccessGranted",
  namespace = "caretrack",
)
public data class FamilyMemberAccessGranted(
  public val familyMemberEmail: String,
  public val accessLevel: String,
  @EventTag(key = "Patient")
  public val patientId: String,
)
