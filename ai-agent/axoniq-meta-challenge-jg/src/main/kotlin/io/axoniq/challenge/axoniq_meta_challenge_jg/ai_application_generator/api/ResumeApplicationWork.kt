package io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ResumeApplicationWork",
  namespace = "axoniq-meta-challenge-jg",
)
public data class ResumeApplicationWork(
  @TargetEntityId
  public val participantId: String,
  public val sessionToken: String,
)
