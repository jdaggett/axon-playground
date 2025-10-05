package io.axoniq.build.dance_test.booking_access_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "UpdateBookingAccess",
  namespace = "dance-test",
)
public data class UpdateBookingAccess(
  public val instructorId: String,
  public val newAccessStatus: String,
  public val reason: String?,
  @TargetEntityId
  public val studentId: String,
)
