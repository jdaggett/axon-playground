package io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class InvalidCredentials(
  message: String,
) : IllegalArgumentException(message)
