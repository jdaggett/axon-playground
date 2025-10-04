package io.axoniq.build.jupiter_wheels.drop_off_zones_view.api

import kotlin.Int
import kotlin.String

public data class ZoneDetailsResult(
  public val location: String,
  public val capacity: Int,
  public val availableSpaces: Int,
  public val zoneName: String,
  public val zoneId: String,
)
