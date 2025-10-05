package io.axoniq.build.dance_test.lesson_package_management.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import org.axonframework.commandhandling.annotations.Command
import org.axonframework.modelling.annotations.TargetEntityId

@Command(
  name = "CreateCustomLessonPackage",
  namespace = "dance-test",
)
public data class CreateCustomLessonPackage(
  public val instructorId: String,
  public val lessonCount: Int,
  @TargetEntityId
  public val packageId: String,
  public val lessonDuration: Int,
  public val studentId: String,
  public val price: Double,
)
