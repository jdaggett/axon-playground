package io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.exception

import kotlin.IllegalArgumentException
import kotlin.String

public class GallerySharingFailed(
  message: String,
) : IllegalArgumentException(message)
