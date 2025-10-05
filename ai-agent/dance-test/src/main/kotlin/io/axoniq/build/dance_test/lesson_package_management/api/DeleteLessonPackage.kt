package io.axoniq.build.dance_test.lesson_package_management.api

import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "DeleteLessonPackage",
  namespace = "dance-test",
)
public data class DeleteLessonPackage(
  @TargetEntityId
  public val packageId: String,
  public val studentId: String,
)
