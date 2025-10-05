package io.axoniq.build.caretrack.doctor_registration.api

import kotlin.Boolean
import kotlin.String

public data class DoctorRegistrationResult(
  public val registrationSuccessful: Boolean,
  public val doctorId: String,
)
