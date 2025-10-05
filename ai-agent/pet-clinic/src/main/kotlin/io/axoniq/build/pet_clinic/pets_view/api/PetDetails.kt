package io.axoniq.build.pet_clinic.pets_view.api

import java.util.Date

/**
 * Data transfer object representing pet details in the Pets View component.
 */
data class PetDetails(
    val petId: String,
    val name: String,
    val birthday: Date,
    val type: String
)

