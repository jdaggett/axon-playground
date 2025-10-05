package io.axoniq.build.caretrack.family_member_view

import io.axoniq.build.caretrack.family_member_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Family Member View Component - handles family member list and permission queries.
 * This component maintains a read model of family members and their access permissions
 * by listening to family member related events and providing query handlers for
 * retrieving family member data.
 */
@Component
class FamilyMemberViewComponent(
    private val familyMemberRepository: FamilyMemberRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyMemberViewComponent::class.java)
    }

    /**
     * Handles queries for specific member permission details.
     * Returns detailed permission information for a family member.
     */
    @QueryHandler
    fun handle(query: MemberPermissionDetails): MemberPermissionDetailsResult {
        logger.info("Handling MemberPermissionDetails query for patient: ${query.patientId}, member: ${query.familyMemberEmail}")

        val familyMember = familyMemberRepository.findByPatientIdAndFamilyMemberEmail(
            query.patientId, 
            query.familyMemberEmail
        )

        return if (familyMember != null) {
            // Map access level to specific permissions
            val (canViewTreatments, canViewDiagnoses, canViewAppointments) = when (familyMember.accessLevel) {
                "FULL" -> Triple(true, true, true)
                "LIMITED" -> Triple(true, false, true)
                "APPOINTMENTS_ONLY" -> Triple(false, false, true)
                else -> Triple(false, false, false)
            }

            MemberPermissionDetailsResult(
                familyMemberEmail = familyMember.familyMemberEmail,
                canViewTreatments = canViewTreatments,
                canViewDiagnoses = canViewDiagnoses,
                canViewAppointments = canViewAppointments,
                accessLevel = familyMember.accessLevel
            )
        } else {
            MemberPermissionDetailsResult(
                familyMemberEmail = query.familyMemberEmail,
                canViewTreatments = false,
                canViewDiagnoses = false,
                canViewAppointments = false,
                accessLevel = "NONE"
            )
        }
    }

    /**
     * Handles queries for family member permissions list.
     * Returns a list of all family members and their access levels for a patient.
     */
    @QueryHandler
    fun handle(query: FamilyMemberPermissions): FamilyMemberPermissionsResult {
        logger.info("Handling FamilyMemberPermissions query for patient: ${query.patientId}")
        
        val familyMembers = familyMemberRepository.findByPatientId(query.patientId)

        val permissions = familyMembers.map { familyMember ->
            FamilyMemberPermission(
                familyMemberEmail = familyMember.familyMemberEmail,
                accessLevel = familyMember.accessLevel
            )
        }

        return FamilyMemberPermissionsResult(permissions = permissions)
    }

    /**
     * Handles queries for family member list.
     * Returns a comprehensive list of all family members with their status and access levels.
     */
    @QueryHandler
    fun handle(query: FamilyMemberList): FamilyMemberListResult {
        logger.info("Handling FamilyMemberList query for patient: ${query.patientId}")

        val familyMembers = familyMemberRepository.findByPatientId(query.patientId)
        
        val members = familyMembers.map { familyMember ->
            FamilyMember(
                email = familyMember.familyMemberEmail,
                accessLevel = familyMember.accessLevel,
                status = familyMember.status
            )
        }

        return FamilyMemberListResult(familyMembers = members)
    }

    /**
     * Handles FamilyMemberPermissionsChanged events.
     * Updates the access level for an existing family member.
     */
    @EventHandler
    fun on(event: FamilyMemberPermissionsChanged) {
        logger.info("Processing FamilyMemberPermissionsChanged event for patient: ${event.patientId}, member: ${event.familyMemberEmail}")

        val existingMember = familyMemberRepository.findByPatientIdAndFamilyMemberEmail(
            event.patientId,
            event.familyMemberEmail
        )

        if (existingMember != null) {
            val updatedMember = existingMember.copy(accessLevel = event.newAccessLevel)
            familyMemberRepository.save(updatedMember)
            logger.info("Updated access level for family member: ${event.familyMemberEmail} to: ${event.newAccessLevel}")
        } else {
            logger.warn("Family member not found for permissions change: ${event.familyMemberEmail}")
        }
    }

    /**
     * Handles FamilyMemberAccessGranted events.
     * Updates the status of a family member to granted and sets their access level.
     */
    @EventHandler
    fun on(event: FamilyMemberAccessGranted) {
        logger.info("Processing FamilyMemberAccessGranted event for patient: ${event.patientId}, member: ${event.familyMemberEmail}")

        val existingMember = familyMemberRepository.findByPatientIdAndFamilyMemberEmail(
            event.patientId,
            event.familyMemberEmail
        )

        if (existingMember != null) {
            val updatedMember = existingMember.copy(
                accessLevel = event.accessLevel,
                status = "GRANTED"
            )
            familyMemberRepository.save(updatedMember)
            logger.info("Granted access to family member: ${event.familyMemberEmail} with level: ${event.accessLevel}")
        } else {
            logger.warn("Family member not found for access grant: ${event.familyMemberEmail}")
        }
    }

    /**
     * Handles FamilyMemberInvitationSent events.
     * Creates a new family member entry with pending status when an invitation is sent.
     */
    @EventHandler
    fun on(event: FamilyMemberInvitationSent) {
        logger.info("Processing FamilyMemberInvitationSent event for patient: ${event.patientId}, member: ${event.familyMemberEmail}")

        val existingMember = familyMemberRepository.findByPatientIdAndFamilyMemberEmail(
            event.patientId,
            event.familyMemberEmail
        )

        if (existingMember == null) {
            val newMember = FamilyMemberEntity(
                patientId = event.patientId,
                familyMemberEmail = event.familyMemberEmail,
                accessLevel = event.accessLevel,
                status = "PENDING"
            )
            familyMemberRepository.save(newMember)
            logger.info("Created new family member invitation: ${event.familyMemberEmail} with access level: ${event.accessLevel}")
        } else {
            val updatedMember = existingMember.copy(
                accessLevel = event.accessLevel,
                status = "PENDING"
            )
            familyMemberRepository.save(updatedMember)
            logger.info("Updated existing family member invitation: ${event.familyMemberEmail}")
        }
    }

    /**
     * Handles FamilyMemberAccessRevoked events.
     * Updates the status of a family member to revoked and removes their access level.
     */
    @EventHandler
    fun on(event: FamilyMemberAccessRevoked) {
        logger.info("Processing FamilyMemberAccessRevoked event for patient: ${event.patientId}, member: ${event.familyMemberEmail}")

        val existingMember = familyMemberRepository.findByPatientIdAndFamilyMemberEmail(
            event.patientId,
            event.familyMemberEmail
        )

        if (existingMember != null) {
            val updatedMember = existingMember.copy(
                accessLevel = "NONE",
                status = "REVOKED"
            )
            familyMemberRepository.save(updatedMember)
            logger.info("Revoked access for family member: ${event.familyMemberEmail}")
        } else {
            logger.warn("Family member not found for access revocation: ${event.familyMemberEmail}")
        }
    }
}

