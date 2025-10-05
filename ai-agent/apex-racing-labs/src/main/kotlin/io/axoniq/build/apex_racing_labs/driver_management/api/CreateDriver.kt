package io.axoniq.build.apex_racing_labs.driver_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateDriver",
  namespace = "apex-racing-labs",
)
public data class CreateDriver(
  public val teamId: String,
  @TargetEntityId
  public val driverId: String,
  public val driverName: String,
)
