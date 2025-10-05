package io.axoniq.build.caretrack.doctor_registration.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command

@Command(
  name = "RegisterDoctor",
  namespace = "caretrack",
)
public data class RegisterDoctor(
  public val firstName: String,
  public val email: String,
  public val medicalLicenseNumber: String,
  public val specialization: String,
  public val lastName: String,
)
