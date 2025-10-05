package io.axoniq.build.caretrack.family_member_view

import jakarta.persistence.*
import org.hibernate.annotations.GenericGenerator

/**
 * JPA entity representing a family member's access to patient data.
 * This entity is used by the Family Member View component to maintain the read model
 * for family member permissions and access levels.
 */
@Entity
@Table(
    name = "family_member_view",
    uniqueConstraints = [UniqueConstraint(columnNames = ["patient_id", "family_member_email"])]
)
data class FamilyMemberEntity(
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    val id: String = "",

    @Column(name = "patient_id", nullable = false)
    val patientId: String = "",

    @Column(name = "family_member_email", nullable = false)
    val familyMemberEmail: String = "",

    @Column(name = "access_level", nullable = false)
    val accessLevel: String = "",

    @Column(name = "status", nullable = false)
    val status: String = ""
)

