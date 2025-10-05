package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class ChallengeAlreadyCompleted(
  message: String,
) : IllegalArgumentException(message)
