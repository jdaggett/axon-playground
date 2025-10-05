package io.axoniq.build.dance_test.session_booking.api

import java.time.LocalDateTime
import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateSessionBooking",
  namespace = "dance-test",
)
public data class CreateSessionBooking(
  public val instructorId: String,
  public val duration: Int,
  public val sessionDate: LocalDateTime,
  public val studentId: String,
  @TargetEntityId
  public val sessionId: String,
)
