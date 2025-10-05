package io.axoniq.build.apex_racing_labs.driver_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "RemoveDriver",
  namespace = "apex-racing-labs",
)
public data class RemoveDriver(
  @TargetEntityId
  public val driverId: String,
)
