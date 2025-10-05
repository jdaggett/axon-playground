package io.axoniq.build.apex_racing_labs.email_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "AccountCreated",
  namespace = "apex-racing-labs",
)
public data class AccountCreated(
  @EventTag(key = "User")
  public val email: String,
  public val verificationToken: String,
)
