package io.axoniq.build.dance_test.lesson_package_management.api

import kotlin.String
import org.axonframework.eventhandling.annotations.Event
import org.axonframework.eventsourcing.annotations.EventTag

@Event(
  name = "LessonPackageDeleted",
  namespace = "dance-test",
)
public data class LessonPackageDeleted(
  @EventTag(key = "LessonPackage")
  public val packageId: String,
)
