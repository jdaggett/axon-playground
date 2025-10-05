package io.axoniq.build.pet_clinic.pets_view.api

/**
 * Result containing a list of pets from the Pets View component.
 */
data class PetsListResult(
    val pets: List<PetDetails>
)

