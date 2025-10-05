package io.axoniq.build.dance_test.calendly_integration.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CancelSessionBooking",
  namespace = "dance-test",
)
public data class CancelSessionBooking(
  public val cancellationTime: LocalDateTime,
  @TargetEntityId
  public val sessionId: String,
)
