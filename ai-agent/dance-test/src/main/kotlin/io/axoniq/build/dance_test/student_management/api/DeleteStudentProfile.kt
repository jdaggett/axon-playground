package io.axoniq.build.dance_test.student_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DeleteStudentProfile",
  namespace = "dance-test",
)
public data class DeleteStudentProfile(
  public val instructorId: String,
  @TargetEntityId
  public val studentId: String,
)
