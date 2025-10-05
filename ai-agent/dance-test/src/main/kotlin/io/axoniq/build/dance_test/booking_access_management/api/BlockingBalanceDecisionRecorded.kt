package io.axoniq.build.dance_test.booking_access_management.api

import kotlin.Boolean
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BlockingBalanceDecisionRecorded",
  namespace = "dance-test",
)
public data class BlockingBalanceDecisionRecorded(
  public val instructorId: String,
  public val preserveBalances: Boolean,
  @EventTag(key = "Student")
  public val studentId: String,
)
