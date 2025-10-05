package io.axoniq.build.caretrack.invitation_view

import io.axoniq.build.caretrack.invitation_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST controller for invitation view operations.
 * Exposes HTTP endpoints for querying invitation information.
 * Component: Invitation View
 */
@RestController
@RequestMapping("/api/invitations")
class InvitationViewController(
    private val queryGateway: QueryGateway
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InvitationViewController::class.java)
    }

    /**
     * REST endpoint to get invitation details by invitation ID.
     * Component: Invitation View
     * 
     * @param id The invitation ID
     * @return CompletableFuture with invitation details
     */
    @GetMapping("/{id}")
    fun getInvitationDetails(@PathVariable id: String): CompletableFuture<InvitationDetailsResult> {
        logger.info("REST request for invitation details, ID: {}", id)
        
        val query = InvitationDetails(invitationId = id)
        return queryGateway.query(query, InvitationDetailsResult::class.java, null)
    }
}