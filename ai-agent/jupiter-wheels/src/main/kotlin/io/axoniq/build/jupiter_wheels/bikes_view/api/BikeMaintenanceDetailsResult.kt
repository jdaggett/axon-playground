package io.axoniq.build.jupiter_wheels.bikes_view.api

import java.time.LocalDateTime
import kotlin.String
import kotlin.collections.List

public data class BikeMaintenanceDetailsResult(
  public val maintenanceHistory: List<String>,
  public val lastInspection: LocalDateTime?,
  public val condition: String,
  public val bikeId: String,
)
