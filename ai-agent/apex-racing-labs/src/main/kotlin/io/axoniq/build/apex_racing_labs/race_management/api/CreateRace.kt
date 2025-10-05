package io.axoniq.build.apex_racing_labs.race_management.api

import java.time.LocalDate
import kotlin.String
import kotlin.collections.List
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateRace",
  namespace = "apex-racing-labs",
)
public data class CreateRace(
  public val participatingDriverIds: List<String>,
  @TargetEntityId
  public val raceId: String,
  public val raceDate: LocalDate,
  public val trackName: String,
)
