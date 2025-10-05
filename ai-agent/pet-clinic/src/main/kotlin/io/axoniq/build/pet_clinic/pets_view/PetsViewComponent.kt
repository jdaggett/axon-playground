package io.axoniq.build.pet_clinic.pets_view

import io.axoniq.build.pet_clinic.pets_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Pets View component that handles queries for pet information and maintains a read model
 * by listening to pet-related events. This component is part of the Pets View system
 * and provides query capabilities for viewing registered pets.
 */
@Component
class PetsViewComponent(
    private val petRepository: PetRepository
) {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PetsViewComponent::class.java)
    }

    /**
     * Handles the PetsList query to retrieve all registered pets.
     * This query handler supports the Pets View component's capability
     * to provide a list of all pets in the system.
     */
    @QueryHandler
    fun handle(query: PetsList): PetsListResult {
        logger.info("Handling PetsList query for Pets View component")

        val pets = petRepository.findAll()
        val petDetails = pets.map { pet ->
            PetDetails(
                petId = pet.petId,
                name = pet.name,
                birthday = pet.birthday,
                type = pet.type
            )
        }

        logger.info("Retrieved ${petDetails.size} pets for PetsList query")
        return PetsListResult(pets = petDetails)
    }

    /**
     * Handles the PetRegistered event to update the read model when a new pet is registered.
     * This event handler is part of the Pets View component's responsibility
     * to maintain an up-to-date view of all registered pets.
     */
    @EventHandler
    fun on(event: PetRegistered) {
        logger.info("Handling PetRegistered event for pet: ${event.petId}")

        val petEntity = PetEntity(
            petId = event.petId,
            name = event.name,
            birthday = event.birthday,
            type = event.type
        )

        petRepository.save(petEntity)
        logger.info("Successfully saved pet ${event.petId} to read model")
    }
}

