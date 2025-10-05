package io.axoniq.build.sleep_on_time.guest_checkout_service.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "InitiateCheckOut",
  namespace = "sleep-on-time",
)
public data class InitiateCheckOut(
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
