package io.axoniq.build.jupiter_wheels.bike_rental_management.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class BikeAlreadyReserved(
  message: String,
) : IllegalArgumentException(message)
