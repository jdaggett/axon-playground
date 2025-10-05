package io.axoniq.build.caretrack.patient_registration.api

import java.time.LocalDate
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "PatientRegistered",
  namespace = "caretrack",
)
public data class PatientRegistered(
  public val firstName: String,
  public val email: String,
  @EventTag(key = "Patient")
  public val patientId: String,
  public val dateOfBirth: LocalDate,
  public val lastName: String,
)
