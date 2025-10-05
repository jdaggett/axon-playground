package io.axoniq.build.jupiter_wheels.user_account_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "UserAccountCreated",
  namespace = "jupiter-wheels",
)
public data class UserAccountCreated(
  @EventTag(key = "User")
  public val userId: String,
  public val email: String,
  public val phoneNumber: String,
  public val name: String,
)
