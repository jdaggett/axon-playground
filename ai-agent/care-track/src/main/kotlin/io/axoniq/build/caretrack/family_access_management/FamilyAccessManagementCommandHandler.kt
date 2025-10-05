package io.axoniq.build.caretrack.family_access_management

import io.axoniq.build.caretrack.family_access_management.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*

/**
 * Command handler for the Family Access Management Service component.
 * Handles family member invitations and access permissions for patients.
 */
class FamilyAccessManagementCommandHandler {
    
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyAccessManagementCommandHandler::class.java)
    }

    /**
     * Handles the InviteFamilyMember command to invite a family member with specific access permissions.
     */
    @CommandHandler
    fun handle(
        command: InviteFamilyMember,
        @InjectEntity state: FamilyAccessManagementState,
        eventAppender: EventAppender
    ): FamilyInvitationResult {
        logger.info("Handling InviteFamilyMember command for patient: ${command.patientId}, family member: ${command.familyMemberEmail}")

        // Check if family member already has access
        if (state.hasFamilyMemberWithEmail(command.familyMemberEmail)) {
            logger.warn("Family member ${command.familyMemberEmail} already has access to patient ${command.patientId}")
            return FamilyInvitationResult(invitationId = "", invitationSent = false)
        }

        val invitationId = UUID.randomUUID().toString()

        val event = FamilyMemberInvitationSent(
            patientId = command.patientId,
            familyMemberEmail = command.familyMemberEmail,
            accessLevel = command.accessLevel,
            invitationId = invitationId
        )

        eventAppender.append(event)
        logger.info("Family member invitation sent for patient: ${command.patientId}, invitation ID: $invitationId")

        return FamilyInvitationResult(invitationId = invitationId, invitationSent = true)
    }

    /**
     * Handles the AcceptFamilyInvitation command to grant access to a family member.
     */
    @CommandHandler
    fun handle(
        command: AcceptFamilyInvitation,
        @InjectEntity state: FamilyAccessManagementState,
        eventAppender: EventAppender
    ): FamilyAcceptanceResult {
        logger.info("Handling AcceptFamilyInvitation command for invitation: ${command.invitationId}, family member: ${command.familyMemberEmail}")

        // Check if invitation exists and is pending
        val invitation = state.getPendingInvitationById(command.invitationId)
        if (invitation == null) {
            logger.warn("No pending invitation found with ID: ${command.invitationId}")
            return FamilyAcceptanceResult(accessGranted = false)
        }

        if (invitation.familyMemberEmail != command.familyMemberEmail) {
            logger.warn("Family member email mismatch for invitation: ${command.invitationId}")
            return FamilyAcceptanceResult(accessGranted = false)
        }

        val event = FamilyMemberAccessGranted(
            familyMemberEmail = command.familyMemberEmail,
            accessLevel = invitation.accessLevel,
            patientId = invitation.patientId
        )

        eventAppender.append(event)
        logger.info("Family member access granted for invitation: ${command.invitationId}")

        return FamilyAcceptanceResult(accessGranted = true)
    }

    /**
     * Handles the DeclineFamilyInvitation command to decline a family invitation.
     */
    @CommandHandler
    fun handle(
        command: DeclineFamilyInvitation,
        @InjectEntity state: FamilyAccessManagementState,
        eventAppender: EventAppender
    ): FamilyDeclineResult {
        logger.info("Handling DeclineFamilyInvitation command for invitation: ${command.invitationId}, family member: ${command.familyMemberEmail}")

        // Check if invitation exists and is pending
        val invitation = state.getPendingInvitationById(command.invitationId)
        if (invitation == null) {
            logger.warn("No pending invitation found with ID: ${command.invitationId}")
            return FamilyDeclineResult(invitationDeclined = false)
        }

        if (invitation.familyMemberEmail != command.familyMemberEmail) {
            logger.warn("Family member email mismatch for invitation: ${command.invitationId}")
            return FamilyDeclineResult(invitationDeclined = false)
        }

        val event = FamilyMemberInvitationDeclined(
            familyMemberEmail = command.familyMemberEmail,
            invitationId = command.invitationId
        )

        eventAppender.append(event)
        logger.info("Family member invitation declined for invitation: ${command.invitationId}")

        return FamilyDeclineResult(invitationDeclined = true)
    }

    /**
     * Handles the ChangeFamilyMemberPermissions command to update access level for an existing family member.
     */
    @CommandHandler
    fun handle(
        command: ChangeFamilyMemberPermissions,
        @InjectEntity state: FamilyAccessManagementState,
        eventAppender: EventAppender
    ): PermissionUpdateResult {
        logger.info("Handling ChangeFamilyMemberPermissions command for patient: ${command.patientId}, family member: ${command.familyMemberEmail}")

        // Check if family member has access
        if (!state.hasFamilyMemberWithEmail(command.familyMemberEmail)) {
            logger.warn("Family member ${command.familyMemberEmail} does not have access to patient ${command.patientId}")
            return PermissionUpdateResult(permissionsUpdated = false)
        }

        // Check if the access level is already the same
        val currentMember = state.getFamilyMemberByEmail(command.familyMemberEmail)
        if (currentMember?.accessLevel == command.newAccessLevel) {
            logger.info("Family member ${command.familyMemberEmail} already has access level: ${command.newAccessLevel}")
            return PermissionUpdateResult(permissionsUpdated = false)
        }

        val event = FamilyMemberPermissionsChanged(
            familyMemberEmail = command.familyMemberEmail,
            newAccessLevel = command.newAccessLevel,
            patientId = command.patientId
        )

        eventAppender.append(event)
        logger.info("Family member permissions changed for patient: ${command.patientId}, family member: ${command.familyMemberEmail}")
        
        return PermissionUpdateResult(permissionsUpdated = true)
    }

    /**
     * Handles the RemoveFamilyMemberAccess command to revoke access from a family member.
     */
    @CommandHandler
    fun handle(
        command: RemoveFamilyMemberAccess,
        @InjectEntity state: FamilyAccessManagementState,
        eventAppender: EventAppender
    ): AccessRemovalResult {
        logger.info("Handling RemoveFamilyMemberAccess command for patient: ${command.patientId}, family member: ${command.familyMemberEmail}")

        // Check if family member has access
        if (!state.hasFamilyMemberWithEmail(command.familyMemberEmail)) {
            logger.warn("Family member ${command.familyMemberEmail} does not have access to patient ${command.patientId}")
            return AccessRemovalResult(accessRemoved = false)
        }

        val event = FamilyMemberAccessRevoked(
            familyMemberEmail = command.familyMemberEmail,
            patientId = command.patientId
        )

        eventAppender.append(event)
        logger.info("Family member access revoked for patient: ${command.patientId}, family member: ${command.familyMemberEmail}")
        
        return AccessRemovalResult(accessRemoved = true)
    }
}

