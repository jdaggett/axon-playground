package io.axoniq.build.jupiter_wheels.emergency_support_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ContactEmergencySupport",
  namespace = "jupiter-wheels",
)
public data class ContactEmergencySupport(
  public val emergencyType: String,
  @TargetEntityId
  public val rentalId: String,
)
