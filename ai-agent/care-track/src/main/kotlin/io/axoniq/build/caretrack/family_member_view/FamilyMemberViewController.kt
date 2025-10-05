package io.axoniq.build.caretrack.family_member_view

import io.axoniq.build.caretrack.family_member_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Family Member View operations.
 * Provides HTTP endpoints for querying family member data and permissions.
 */
@RestController
@RequestMapping("/api/family-members")
class FamilyMemberViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyMemberViewController::class.java)
    }

    /**
     * Get detailed permissions for a specific family member.
     */
    @GetMapping("/{patientId}/permissions/{email}")
    fun getMemberPermissionDetails(
        @PathVariable patientId: String,
        @PathVariable email: String
    ): CompletableFuture<MemberPermissionDetailsResult> {
        logger.info("REST request for member permission details - Patient: $patientId, Member: $email")

        val query = MemberPermissionDetails(patientId = patientId, familyMemberEmail = email)
        return queryGateway.query(query, MemberPermissionDetailsResult::class.java, null)
    }

    /**
     * Get permissions for all family members of a patient.
     */
    @GetMapping("/{patientId}/permissions")
    fun getFamilyMemberPermissions(
        @PathVariable patientId: String
    ): CompletableFuture<FamilyMemberPermissionsResult> {
        logger.info("REST request for family member permissions - Patient: $patientId")

        val query = FamilyMemberPermissions(patientId = patientId)
        return queryGateway.query(query, FamilyMemberPermissionsResult::class.java, null)
    }

    /**
     * Get complete list of family members for a patient.
     */
    @GetMapping("/{patientId}")
    fun getFamilyMemberList(
        @PathVariable patientId: String
    ): CompletableFuture<FamilyMemberListResult> {
        logger.info("REST request for family member list - Patient: $patientId")

        val query = FamilyMemberList(patientId = patientId)
        return queryGateway.query(query, FamilyMemberListResult::class.java, null)
    }
}