package io.axoniq.build.dance_test.session_booking.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class CreditLimitExceeded(
  message: String,
) : IllegalArgumentException(message)
