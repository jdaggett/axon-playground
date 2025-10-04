package io.axoniq.build.jupiter_wheels.payment_options_view.api

import kotlin.String
import org.axonframework.queryhandling.annotations.Query

@Query(
  name = "PaymentRetryOptions",
  namespace = "jupiter-wheels",
)
public data class PaymentRetryOptions(
  public val rentalId: String,
)
