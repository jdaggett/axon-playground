package io.axoniq.build.caretrack.notification_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "FamilyMemberInvitationSent",
  namespace = "caretrack",
)
public data class FamilyMemberInvitationSent(
  public val patientId: String,
  public val familyMemberEmail: String,
  public val accessLevel: String,
  @EventTag(key = "FamilyInvitation")
  public val invitationId: String,
)
