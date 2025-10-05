package io.axoniq.build.pet_clinic.pets_view

import io.axoniq.build.pet_clinic.pets_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for the Pets View component.
 * Exposes HTTP endpoints to query pet information using the QueryGateway.
 * This controller provides external access to the pet read model maintained by the Pets View component.
 */
@RestController
@RequestMapping("/api/pets")
class PetsViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PetsViewController::class.java)
    }

    /**
     * Endpoint to retrieve all registered pets.
     * Uses the PetsList query to fetch all pets from the Pets View component.
     */
    @GetMapping
    fun getAllPets(): CompletableFuture<PetsListResult> {
        logger.info("REST request to get all pets via Pets View component")
        val query = PetsList()
        return queryGateway.query(query, PetsListResult::class.java, null)
    }
}