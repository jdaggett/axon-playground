package io.axoniq.build.pet_clinic.pet_management

import io.axoniq.build.pet_clinic.pet_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import java.util.*

/**
 * Event sourced entity state for Pet Management component.
 * Maintains the current state of pet registration and related information.
 */
@EventSourcedEntity
class PetManagementState {

    private var petId: String? = null
    private var email: String? = null
    private var name: String? = null
    private var birthday: Date? = null
    private var registrationStatus: String = "NOT_REGISTERED"
    private var type: String? = null

    // Getters for accessing private state from command handlers
    fun getPetId(): String? = petId
    fun getEmail(): String? = email
    fun getName(): String? = name
    fun getBirthday(): Date? = birthday
    fun getRegistrationStatus(): String = registrationStatus
    fun getType(): String? = type
    
    @EntityCreator
    constructor()

    /**
     * Evolves state when PetRegistered event is processed.
     * Updates pet information and sets registration status to REGISTERED.
     */
    @EventSourcingHandler
    fun evolve(event: PetRegistered) {
        this.petId = event.petId
        this.email = event.email
        this.name = event.name
        this.birthday = event.birthday
        this.type = event.type
        this.registrationStatus = "REGISTERED"
    }

    companion object {
        /**
         * Builds event criteria for loading events related to this pet.
         * Uses petId tag to filter relevant events.
         */
        @EventCriteriaBuilder
        fun resolveCriteria(petId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("petId", petId))
                .andBeingOneOfTypes(
                    PetRegistered::class.java.name
                )
        }
    }
}

