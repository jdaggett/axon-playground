package io.axoniq.build.pet_clinic.pets_view

import jakarta.persistence.*
import java.util.Date

/**
 * JPA Entity representing a Pet in the pets view read model.
 * This entity stores pet information for query purposes in the Pets View component.
 */
@Entity
@Table(name = "pets")
data class PetEntity(
    @Id
    @Column(name = "pet_id")
    val petId: String = "",

    @Column(name = "name", nullable = false)
    val name: String = "",

    @Column(name = "birthday", nullable = false)
    @Temporal(TemporalType.DATE)
    val birthday: Date = Date(),

    @Column(name = "type", nullable = false)
    val type: String = ""
) {
    // No-argument constructor required by JPA
    constructor() : this("", "", Date(), "")
}

