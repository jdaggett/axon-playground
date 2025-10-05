package io.axoniq.build.dance_test.payment_management.api

import kotlin.Double
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "AdjustStudentBalance",
  namespace = "dance-test",
)
public data class AdjustStudentBalance(
  public val adjustmentAmount: Double,
  @TargetEntityId
  public val studentId: String,
  public val adjustmentReason: String,
)
