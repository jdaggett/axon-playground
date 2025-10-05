package io.axoniq.build.apex_racing_labs.user_registration.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class VerificationTokenExpired(
  message: String,
) : IllegalArgumentException(message)
