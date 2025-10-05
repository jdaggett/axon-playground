package io.axoniq.build.dance_test.lesson_package_management.api

import kotlin.Double
import kotlin.Int
import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "CustomLessonPackageCreated",
  namespace = "dance-test",
)
public data class CustomLessonPackageCreated(
  public val instructorId: String,
  public val lessonCount: Int,
  @EventTag(key = "LessonPackage")
  public val packageId: String,
  public val lessonDuration: Int,
  public val studentId: String,
  public val price: Double,
)
