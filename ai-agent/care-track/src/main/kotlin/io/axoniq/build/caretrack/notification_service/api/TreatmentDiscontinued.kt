package io.axoniq.build.caretrack.notification_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TreatmentDiscontinued",
  namespace = "caretrack",
)
public data class TreatmentDiscontinued(
  public val doctorId: String,
  public val reason: String?,
  @EventTag(key = "Patient")
  public val patientId: String,
  public val treatmentId: String,
)
