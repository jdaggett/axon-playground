package io.axoniq.build.dance_test.transaction_history_view.api

import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "BalanceAdjustmentRecorded",
  namespace = "dance-test",
)
public data class BalanceAdjustmentRecorded(
  public val adjustmentAmount: Double,
  @EventTag(key = "Student")
  public val studentId: String,
  public val adjustmentReason: String,
)
