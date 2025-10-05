package io.axoniq.build.sleep_on_time.container_access.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class ContainerAlreadyOccupied(
  message: String,
) : IllegalArgumentException(message)
