package io.axoniq.build.caretrack.notification_service.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "TreatmentPrescribed",
  namespace = "caretrack",
)
public data class TreatmentPrescribed(
  public val doctorId: String,
  public val frequency: String,
  public val dosage: String,
  @EventTag(key = "Patient")
  public val patientId: String,
  public val medicationName: String,
  public val duration: String,
  public val treatmentId: String,
)
