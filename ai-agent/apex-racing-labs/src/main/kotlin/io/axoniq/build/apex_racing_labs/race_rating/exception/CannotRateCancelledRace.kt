package io.axoniq.build.apex_racing_labs.race_rating.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class CannotRateCancelledRace(
  message: String,
) : IllegalArgumentException(message)
