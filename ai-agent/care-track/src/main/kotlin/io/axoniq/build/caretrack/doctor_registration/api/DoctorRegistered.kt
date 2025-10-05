package io.axoniq.build.caretrack.doctor_registration.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "DoctorRegistered",
  namespace = "caretrack",
)
public data class DoctorRegistered(
  public val firstName: String,
  public val email: String,
  public val medicalLicenseNumber: String,
  public val specialization: String,
  public val lastName: String,
  @EventTag(key = "Doctor")
  public val doctorId: String,
)
