package io.axoniq.build.caretrack.invitation_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for InvitationEntity.
 * Provides data access operations for invitation entities.
 * Component: Invitation View
 */
@Repository
interface InvitationRepository : JpaRepository<InvitationEntity, String> {
    
    /**
     * Finds an invitation by its invitation ID.
     * @param invitationId The invitation ID to search for
     * @return The invitation entity if found, null otherwise
     */
    fun findByInvitationId(invitationId: String): InvitationEntity?
}

