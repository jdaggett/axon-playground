package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api

import kotlin.Boolean
import kotlin.String

public data class ParticipantLoginResult(
  public val participantId: String,
  public val isSuccessful: Boolean,
)
