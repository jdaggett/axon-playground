package io.axoniq.build.apex_racing_labs.user_registration.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AccountMarkedUnverified",
  namespace = "apex-racing-labs",
)
public data class AccountMarkedUnverified(
  @EventTag(key = "User")
  public val email: String,
)
