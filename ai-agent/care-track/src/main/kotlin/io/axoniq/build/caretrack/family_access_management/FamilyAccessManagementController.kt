package io.axoniq.build.caretrack.family_access_management

import io.axoniq.build.caretrack.family_access_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Family Access Management Service component.
 * Provides endpoints for managing family member invitations and access permissions.
 */
@RestController
@RequestMapping("/api/family-access")
class FamilyAccessManagementController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyAccessManagementController::class.java)
    }

    /**
     * Invites a family member with specific access permissions.
     */
    @PostMapping("/invite")
    fun inviteFamilyMember(@RequestBody command: InviteFamilyMember): ResponseEntity<String> {
        logger.info("Dispatching InviteFamilyMember command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Family member invitation sent")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch InviteFamilyMember command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to send family member invitation")
        }
    }

    /**
     * Accepts a family invitation to grant access.
     */
    @PostMapping("/accept")
    fun acceptFamilyInvitation(@RequestBody command: AcceptFamilyInvitation): ResponseEntity<String> {
        logger.info("Dispatching AcceptFamilyInvitation command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Family invitation accepted")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch AcceptFamilyInvitation command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to accept family invitation")
        }
    }

    /**
     * Declines a family invitation.
     */
    @PostMapping("/decline")
    fun declineFamilyInvitation(@RequestBody command: DeclineFamilyInvitation): ResponseEntity<String> {
        logger.info("Dispatching DeclineFamilyInvitation command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Family invitation declined")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch DeclineFamilyInvitation command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to decline family invitation")
        }
    }

    /**
     * Changes family member access permissions.
     */
    @PutMapping("/permissions")
    fun changeFamilyMemberPermissions(@RequestBody command: ChangeFamilyMemberPermissions): ResponseEntity<String> {
        logger.info("Dispatching ChangeFamilyMemberPermissions command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Family member permissions updated")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch ChangeFamilyMemberPermissions command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to update family member permissions")
        }
    }

    /**
     * Removes family member access.
     */
    @DeleteMapping("/access")
    fun removeFamilyMemberAccess(@RequestBody command: RemoveFamilyMemberAccess): ResponseEntity<String> {
        logger.info("Dispatching RemoveFamilyMemberAccess command: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Family member access removed")
        } catch (ex: Exception) {
            logger.error("Failed to dispatch RemoveFamilyMemberAccess command", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to remove family member access")
        }
    }
}

