package io.axoniq.build.caretrack.invitation_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing an invitation in the invitation view.
 * This entity stores invitation details for family member invitations.
 * Component: Invitation View
 */
@Entity
@Table(name = "invitations")
data class InvitationEntity(
    @Id
    @Column(name = "invitation_id")
    val invitationId: String = "",

    @Column(name = "patient_id", nullable = false)
    val patientId: String = "",
    
    @Column(name = "family_member_email", nullable = false)
    val familyMemberEmail: String = "",
    
    @Column(name = "access_level", nullable = false)
    val accessLevel: String = "",

    @Column(name = "status", nullable = false)
    val status: String = "PENDING",

    @Column(name = "invitation_date", nullable = false)
    val invitationDate: LocalDate = LocalDate.now()
)

