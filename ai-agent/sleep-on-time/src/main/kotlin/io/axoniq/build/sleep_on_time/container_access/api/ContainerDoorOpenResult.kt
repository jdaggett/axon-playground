package io.axoniq.build.sleep_on_time.container_access.api

import kotlin.Boolean

public data class ContainerDoorOpenResult(
  public val success: Boolean,
  public val unlockRequested: Boolean,
)
