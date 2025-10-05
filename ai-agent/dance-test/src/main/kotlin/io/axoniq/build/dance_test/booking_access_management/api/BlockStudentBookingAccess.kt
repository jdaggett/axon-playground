package io.axoniq.build.dance_test.booking_access_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "BlockStudentBookingAccess",
  namespace = "dance-test",
)
public data class BlockStudentBookingAccess(
  public val instructorId: String,
  public val blockingReason: String,
  @TargetEntityId
  public val studentId: String,
)
