package io.axoniq.build.dance_test.reporting_analytics_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "StudentProfileCreated",
  namespace = "dance-test",
)
public data class StudentProfileCreated(
  public val instructorId: String,
  public val name: String,
  @EventTag(key = "Student")
  public val studentId: String,
  public val phone: String,
)
