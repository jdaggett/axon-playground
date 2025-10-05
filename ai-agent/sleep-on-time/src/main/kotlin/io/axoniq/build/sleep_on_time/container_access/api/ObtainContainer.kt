package io.axoniq.build.sleep_on_time.container_access.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "ObtainContainer",
  namespace = "sleep-on-time",
)
public data class ObtainContainer(
  public val bookingId: String,
  public val guestId: String,
  public val containerId: String,
) {
  @TargetEntityId
  public fun modelIdentifier(): TargetIdentifier = TargetIdentifier(bookingId, guestId, containerId)

  public data class TargetIdentifier(
    public val bookingId: String,
    public val guestId: String,
    public val containerId: String,
  )
}
