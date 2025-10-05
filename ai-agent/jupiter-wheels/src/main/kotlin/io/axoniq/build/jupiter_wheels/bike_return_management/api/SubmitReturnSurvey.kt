package io.axoniq.build.jupiter_wheels.bike_return_management.api

import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "SubmitReturnSurvey",
  namespace = "jupiter-wheels",
)
public data class SubmitReturnSurvey(
  public val feedback: String?,
  public val rating: Int,
  @TargetEntityId
  public val rentalId: String,
)
