package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class BrowserSessionLost(
  message: String,
) : IllegalArgumentException(message)
