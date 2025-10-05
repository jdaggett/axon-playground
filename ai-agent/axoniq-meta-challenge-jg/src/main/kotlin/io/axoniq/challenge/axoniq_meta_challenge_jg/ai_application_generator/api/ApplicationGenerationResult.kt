package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api

import kotlin.Boolean
import kotlin.String

public data class ApplicationGenerationResult(
  public val applicationId: String?,
  public val isSuccessful: Boolean,
)
