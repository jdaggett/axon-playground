package io.axoniq.build.dance_test.session_calendar_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "SessionDetails",
  namespace = "dance-test",
)
public data class SessionDetails(
  public val sessionId: String,
)
