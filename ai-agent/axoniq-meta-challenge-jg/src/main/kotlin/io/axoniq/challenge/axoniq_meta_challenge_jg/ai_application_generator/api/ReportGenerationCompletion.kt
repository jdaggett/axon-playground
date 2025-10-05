package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ReportGenerationCompletion",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ReportGenerationCompletion(
  public val applicationId: String,
  @TargetEntityId
  public val participantId: String,
  public val isSuccessful: Boolean,
)
