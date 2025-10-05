package io.axoniq.build.dance_test.lesson_package_management.api

import kotlin.Double
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TransactionRecordCreated",
  namespace = "dance-test",
)
public data class TransactionRecordCreated(
  public val amount: Double,
  public val description: String,
  public val transactionType: String,
  @EventTag(key = "Student")
  public val studentId: String,
)
