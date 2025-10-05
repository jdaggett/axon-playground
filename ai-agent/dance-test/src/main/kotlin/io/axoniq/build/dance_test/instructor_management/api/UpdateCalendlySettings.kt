package io.axoniq.build.dance_test.instructor_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "UpdateCalendlySettings",
  namespace = "dance-test",
)
public data class UpdateCalendlySettings(
  public val availabilitySettings: String,
  public val calendlyAccountId: String,
  @TargetEntityId
  public val instructorId: String,
)
