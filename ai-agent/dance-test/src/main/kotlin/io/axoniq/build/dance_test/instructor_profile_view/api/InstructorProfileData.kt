package io.axoniq.build.dance_test.instructor_profile_view.api

import kotlin.String
import kotlin.collections.List

public data class InstructorProfileData(
  public val instructorId: String,
  public val calendlyAccountId: String?,
  public val email: String,
  public val specialties: List<String>,
  public val calendlyIntegrationStatus: String,
  public val phone: String,
)
