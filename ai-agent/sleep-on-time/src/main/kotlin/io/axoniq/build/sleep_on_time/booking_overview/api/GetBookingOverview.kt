package io.axoniq.build.sleep_on_time.booking_overview.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "GetBookingOverview",
  namespace = "sleep-on-time",
)
public data class GetBookingOverview(
  public val bookingId: String,
  public val guestId: String,
)
