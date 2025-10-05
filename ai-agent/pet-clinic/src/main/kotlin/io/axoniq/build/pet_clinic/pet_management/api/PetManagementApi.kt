package io.axoniq.build.pet_clinic.pet_management.api

import java.util.*

/**
 * Command to register a new pet.
 */
data class RegisterPet(
    val petId: String,
    val email: String,
    val name: String,
    val birthday: Date,
    val type: String
)

/**
 * Event published when a pet is registered.
 */
data class PetRegistered(
    val petId: String,
    val email: String,
    val name: String,
    val birthday: Date,
    val type: String
)