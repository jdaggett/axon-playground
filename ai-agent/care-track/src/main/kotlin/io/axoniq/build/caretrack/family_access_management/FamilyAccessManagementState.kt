package io.axoniq.build.caretrack.family_access_management

import io.axoniq.build.caretrack.family_access_management.api.*
import org.axonframework.eventstreaming.EventCriteria
import org.axonframework.eventstreaming.Tag
import org.axonframework.eventsourcing.annotations.EventCriteriaBuilder
import org.axonframework.eventsourcing.annotations.EventSourcedEntity
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Event-sourced entity representing the state of family access management for a patient.
 * Tracks family members, their access levels, and pending invitations.
 */
@EventSourcedEntity
class FamilyAccessManagementState {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyAccessManagementState::class.java)
        
        @EventCriteriaBuilder
        fun resolveCriteria(patientId: String): EventCriteria {
            return EventCriteria
                .havingTags(Tag.of("Patient", patientId))
                .andBeingOneOfTypes(
                    FamilyMemberInvitationSent::class.java.name,
                    FamilyMemberAccessGranted::class.java.name,
                    FamilyMemberInvitationDeclined::class.java.name,
                    FamilyMemberPermissionsChanged::class.java.name,
                    FamilyMemberAccessRevoked::class.java.name
                )
        }
    }

    private var patientId: String? = null
    private val familyMembers = mutableListOf<FamilyMember>()
    private val pendingInvitations = mutableListOf<PendingInvitation>()

    @EntityCreator
    constructor()

    fun getPatientId(): String? = patientId

    fun getFamilyMembers(): List<FamilyMember> = familyMembers.toList()

    fun hasFamilyMemberWithEmail(email: String): Boolean {
        return familyMembers.any { it.email == email && it.status == "ACTIVE" }
    }

    fun getFamilyMemberByEmail(email: String): FamilyMember? {
        return familyMembers.find { it.email == email && it.status == "ACTIVE" }
    }

    fun getPendingInvitationById(invitationId: String): PendingInvitation? {
        return pendingInvitations.find { it.invitationId == invitationId }
    }

    /**
     * Handles FamilyMemberInvitationSent event to track pending invitations.
     */
    @EventSourcingHandler
    fun evolve(event: FamilyMemberInvitationSent) {
        logger.debug("Evolving state for FamilyMemberInvitationSent: ${event.invitationId}")

        if (this.patientId == null) {
            this.patientId = event.patientId
        }

        pendingInvitations.add(
            PendingInvitation(
                invitationId = event.invitationId,
                patientId = event.patientId,
                familyMemberEmail = event.familyMemberEmail,
                accessLevel = event.accessLevel
            )
        )
    }

    /**
     * Handles FamilyMemberAccessGranted event to grant access to a family member.
     */
    @EventSourcingHandler
    fun evolve(event: FamilyMemberAccessGranted) {
        logger.debug("Evolving state for FamilyMemberAccessGranted: ${event.familyMemberEmail}")

        if (this.patientId == null) {
            this.patientId = event.patientId
        }

        // Remove pending invitation
        pendingInvitations.removeIf { it.familyMemberEmail == event.familyMemberEmail }

        // Add or update family member
        val existingIndex = familyMembers.indexOfFirst { it.email == event.familyMemberEmail }
        if (existingIndex >= 0) {
            familyMembers[existingIndex] = familyMembers[existingIndex].copy(
                accessLevel = event.accessLevel,
                status = "ACTIVE"
            )
        } else {
            familyMembers.add(
                FamilyMember(
                    email = event.familyMemberEmail,
                    accessLevel = event.accessLevel,
                    status = "ACTIVE"
                )
            )
        }
    }

    /**
     * Handles FamilyMemberInvitationDeclined event to remove declined invitations.
     */
    @EventSourcingHandler
    fun evolve(event: FamilyMemberInvitationDeclined) {
        logger.debug("Evolving state for FamilyMemberInvitationDeclined: ${event.invitationId}")

        // Remove the declined invitation
        pendingInvitations.removeIf { it.invitationId == event.invitationId }
    }

    /**
     * Handles FamilyMemberPermissionsChanged event to update family member access level.
     */
    @EventSourcingHandler
    fun evolve(event: FamilyMemberPermissionsChanged) {
        logger.debug("Evolving state for FamilyMemberPermissionsChanged: ${event.familyMemberEmail}")

        if (this.patientId == null) {
            this.patientId = event.patientId
        }

        val existingIndex = familyMembers.indexOfFirst { it.email == event.familyMemberEmail }
        if (existingIndex >= 0) {
            familyMembers[existingIndex] = familyMembers[existingIndex].copy(
                accessLevel = event.newAccessLevel
            )
        }
    }

    /**
     * Handles FamilyMemberAccessRevoked event to remove family member access.
     */
    @EventSourcingHandler
    fun evolve(event: FamilyMemberAccessRevoked) {
        logger.debug("Evolving state for FamilyMemberAccessRevoked: ${event.familyMemberEmail}")

        if (this.patientId == null) {
            this.patientId = event.patientId
        }

        // Remove the family member or mark as inactive
        familyMembers.removeIf { it.email == event.familyMemberEmail }
    }

    /**
     * Inner class representing a family member with access permissions.
     */
    data class FamilyMember(
        val email: String,
        val accessLevel: String,
        val status: String
    )
    
    /**
     * Inner class representing a pending invitation.
     */
    data class PendingInvitation(
        val invitationId: String,
        val patientId: String,
        val familyMemberEmail: String,
        val accessLevel: String
    )
}

