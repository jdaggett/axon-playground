package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class AIGenerationFailed(
  message: String,
) : IllegalArgumentException(message)
