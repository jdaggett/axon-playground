package io.axoniq.build.pet_clinic.pets_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for Pet entities in the Pets View component.
 * Provides CRUD operations for pets in the read model.
 */
@Repository
interface PetRepository : JpaRepository<PetEntity, String> {

    /**
     * Find pets by type
     */
    fun findByType(type: String): List<PetEntity>

    /**
     * Find pets by name
     */
    fun findByName(name: String): List<PetEntity>
}

