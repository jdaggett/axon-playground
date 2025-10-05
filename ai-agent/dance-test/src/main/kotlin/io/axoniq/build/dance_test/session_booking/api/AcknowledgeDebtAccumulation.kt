package io.axoniq.build.dance_test.session_booking.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "AcknowledgeDebtAccumulation",
  namespace = "dance-test",
)
public data class AcknowledgeDebtAccumulation(
  @TargetEntityId
  public val studentId: String,
  public val sessionId: String,
)
