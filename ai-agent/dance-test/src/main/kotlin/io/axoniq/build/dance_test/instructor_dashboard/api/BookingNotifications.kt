package io.axoniq.build.dance_test.instructor_dashboard.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "BookingNotifications",
  namespace = "dance-test",
)
public data class BookingNotifications(
  public val instructorId: String,
)
