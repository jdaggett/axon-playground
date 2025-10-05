package io.axoniq.build.dance_test.instructor_profile_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity for instructor profile data storage.
 * Stores instructor profile information for the Instructor Profile View component.
 */
@Entity
@Table(name = "instructor_profiles")
data class InstructorProfileEntity(
    @Id
    val instructorId: String,
    
    @Column(nullable = true)
    val calendlyAccountId: String?,

    @Column(nullable = false)
    val email: String,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "instructor_specialties",
        joinColumns = [JoinColumn(name = "instructor_id")]
    )
    @Column(name = "specialty")
    val specialties: MutableList<String> = mutableListOf(),
    
    @Column(nullable = false)
    val calendlyIntegrationStatus: String = "NOT_CONNECTED",

    @Column(nullable = false)
    val phone: String
) {
    constructor() : this("", null, "", mutableListOf(), "NOT_CONNECTED", "")
}

