package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "GenerateAIApplication",
  namespace = "axoniq-meta-challenge-jg",
)
public data class GenerateAIApplication(
  @TargetEntityId
  public val participantId: String,
  public val applicationParameters: String,
)
