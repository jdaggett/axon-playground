package io.axoniq.build.caretrack.invitation_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "InvitationDetails",
  namespace = "caretrack",
)
public data class InvitationDetails(
  public val invitationId: String,
)
