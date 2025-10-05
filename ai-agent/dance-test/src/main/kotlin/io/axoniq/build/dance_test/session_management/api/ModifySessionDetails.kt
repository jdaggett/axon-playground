package io.axoniq.build.dance_test.session_management.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ModifySessionDetails",
  namespace = "dance-test",
)
public data class ModifySessionDetails(
  public val newDuration: Int?,
  public val newSessionDate: LocalDateTime?,
  @TargetEntityId
  public val sessionId: String,
)
