package io.axoniq.build.caretrack.invitation_view.api

import java.time.LocalDate
import kotlin.String

public data class InvitationDetailsResult(
  public val invitationDate: LocalDate,
  public val patientName: String,
  public val accessLevel: String,
  public val invitationId: String,
)
