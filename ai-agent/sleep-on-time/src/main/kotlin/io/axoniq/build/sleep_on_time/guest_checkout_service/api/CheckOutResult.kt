package io.axoniq.build.sleep_on_time.guest_checkout_service.api

import kotlin.Boolean

public data class CheckOutResult(
  public val success: Boolean,
  public val accessKeyWithdrawn: Boolean,
)
