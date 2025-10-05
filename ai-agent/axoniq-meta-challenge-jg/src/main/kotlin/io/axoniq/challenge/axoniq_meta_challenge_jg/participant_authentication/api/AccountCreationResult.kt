package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api

import kotlin.Boolean
import kotlin.String

public data class AccountCreationResult(
  public val participantId: String,
  public val isSuccessful: Boolean,
)
