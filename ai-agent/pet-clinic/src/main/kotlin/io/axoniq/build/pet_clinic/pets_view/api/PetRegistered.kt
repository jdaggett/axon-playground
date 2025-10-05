package io.axoniq.build.pet_clinic.pets_view.api

import java.util.Date

/**
 * Event indicating that a pet has been registered in the system.
 */
data class PetRegistered(
    val petId: String,
    val name: String,
    val birthday: Date,
    val type: String
)