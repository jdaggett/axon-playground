package io.axoniq.build.jupiter_wheels.drop_off_zones_view.api

import kotlin.collections.List

public data class AvailableDropOffZonesList(
  public val zones: List<DropOffZone>,
)
