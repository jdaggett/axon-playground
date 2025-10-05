package io.axoniq.build.dance_test.instructor_management.api

import kotlin.String
import kotlin.collections.List
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateInstructorProfile",
  namespace = "dance-test",
)
public data class CreateInstructorProfile(
  public val email: String,
  public val specialties: List<String>,
  @TargetEntityId
  public val instructorId: String,
  public val phone: String,
)
