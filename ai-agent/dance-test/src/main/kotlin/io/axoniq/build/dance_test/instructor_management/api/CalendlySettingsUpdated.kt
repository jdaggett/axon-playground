package io.axoniq.build.dance_test.instructor_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "CalendlySettingsUpdated",
  namespace = "dance-test",
)
public data class CalendlySettingsUpdated(
  public val calendlyAccountId: String,
  @EventTag(key = "Instructor")
  public val instructorId: String,
)
