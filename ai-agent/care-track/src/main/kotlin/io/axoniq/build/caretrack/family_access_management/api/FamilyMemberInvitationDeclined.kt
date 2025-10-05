package io.axoniq.build.caretrack.family_access_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FamilyMemberInvitationDeclined",
  namespace = "caretrack",
)
public data class FamilyMemberInvitationDeclined(
  public val familyMemberEmail: String,
  @EventTag(key = "FamilyInvitation")
  public val invitationId: String,
)
