package io.axoniq.build.caretrack.invitation_view

import io.axoniq.build.caretrack.invitation_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * View component handling family member invitation queries.
 * This component maintains a read model of invitations by listening to events
 * and provides query handling for invitation details.
 * Component: Invitation View
 */
@Component
class InvitationViewComponent(
    private val invitationRepository: InvitationRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InvitationViewComponent::class.java)
    }

    /**
     * Handles queries for invitation details.
     * Returns detailed information about a specific invitation.
     * Component: Invitation View
     * 
     * @param query The invitation details query containing the invitation ID
     * @return InvitationDetailsResult with invitation information
     */
    @QueryHandler
    fun handle(query: InvitationDetails): InvitationDetailsResult {
        logger.info("Handling InvitationDetails query for invitation ID: {}", query.invitationId)

        val invitation = invitationRepository.findByInvitationId(query.invitationId)
            ?: throw IllegalArgumentException("Invitation not found with ID: ${query.invitationId}")

        return InvitationDetailsResult(
            invitationDate = invitation.invitationDate,
            patientName = "", // Patient name not available in current model
            accessLevel = invitation.accessLevel,
            invitationId = invitation.invitationId
        )
    }

    /**
     * Event handler for FamilyMemberInvitationSent events.
     * Creates a new invitation entry in the read model when a family member invitation is sent.
     * Component: Invitation View
     * 
     * @param event The FamilyMemberInvitationSent event
     */
    @EventHandler
    fun on(event: FamilyMemberInvitationSent) {
        logger.info("Handling FamilyMemberInvitationSent event for invitation ID: {}", event.invitationId)

        val invitation = InvitationEntity(
            invitationId = event.invitationId,
            patientId = event.patientId,
            familyMemberEmail = event.familyMemberEmail,
            accessLevel = event.accessLevel,
            status = "SENT"
        )

        invitationRepository.save(invitation)
        logger.debug("Created invitation entity for ID: {}", event.invitationId)
    }

    /**
     * Event handler for FamilyMemberInvitationDeclined events.
     * Updates the invitation status when a family member declines an invitation.
     * Component: Invitation View
     * 
     * @param event The FamilyMemberInvitationDeclined event
     */
    @EventHandler
    fun on(event: FamilyMemberInvitationDeclined) {
        logger.info("Handling FamilyMemberInvitationDeclined event for invitation ID: {}", event.invitationId)
        
        val invitation = invitationRepository.findByInvitationId(event.invitationId)
        if (invitation != null) {
            val updatedInvitation = invitation.copy(status = "DECLINED")
            invitationRepository.save(updatedInvitation)
            logger.debug("Updated invitation status to DECLINED for ID: {}", event.invitationId)
        } else {
            logger.warn("Invitation not found for decline event, ID: {}", event.invitationId)
        }
    }
}

