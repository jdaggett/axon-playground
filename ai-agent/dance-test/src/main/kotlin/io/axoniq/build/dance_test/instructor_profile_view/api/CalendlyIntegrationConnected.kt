package io.axoniq.build.dance_test.instructor_profile_view.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "CalendlyIntegrationConnected",
  namespace = "dance-test",
)
public data class CalendlyIntegrationConnected(
  public val calendlyAccountId: String,
  @EventTag(key = "Instructor")
  public val instructorId: String,
)
