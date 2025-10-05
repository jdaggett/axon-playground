package io.axoniq.build.caretrack.notification_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PatientAccountDeleted",
  namespace = "caretrack",
)
public data class PatientAccountDeleted(
  @EventTag(key = "Patient")
  public val patientId: String,
)
