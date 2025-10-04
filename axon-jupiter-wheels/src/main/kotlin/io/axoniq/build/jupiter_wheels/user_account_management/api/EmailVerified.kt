package io.axoniq.build.jupiter_wheels.user_account_management.api

import java.time.LocalDateTime
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "EmailVerified",
  namespace = "jupiter-wheels",
)
public data class EmailVerified(
  @EventTag(key = "User")
  public val userId: String,
  public val verificationDate: LocalDateTime,
)
