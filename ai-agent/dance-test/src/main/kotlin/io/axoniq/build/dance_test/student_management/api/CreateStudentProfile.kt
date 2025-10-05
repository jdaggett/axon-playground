package io.axoniq.build.dance_test.student_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateStudentProfile",
  namespace = "dance-test",
)
public data class CreateStudentProfile(
  public val instructorId: String,
  public val name: String,
  @TargetEntityId
  public val studentId: String,
  public val phone: String,
)
