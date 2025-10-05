package io.axoniq.build.pet_clinic.mail_notification_system

import org.springframework.stereotype.Service
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Event class for pet registration notifications
 */
data class PetRegistered(
    val petId: String,
    val ownerId: String,
    val petName: String,
    val ownerEmail: String
)

/**
 * Command class for owner notifications
 */
data class NotifyOwner(
    val petId: String,
    val ownerId: String,
    val petName: String,
    val ownerEmail: String
)

/**
 * External system component for the Mail Notification System.
 * This service handles pet registration events and triggers owner notification actions
 * through the external mail notification system.
 *
 * Component: Mail Notification System
 * Type: EXTERNAL_SYSTEM
 * Description: External system that processes pet registration notifications
 */
@Service
class MailNotificationSystemService(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(MailNotificationSystemService::class.java)

    /**
     * Handles PetRegistered events to trigger owner notification via external mail system.
     * This event handler processes pet registration notifications and sends appropriate
     * commands back to the system to notify the pet owner.
     * 
     * External System Action: Send Owner Notification
     * Triggered by: PetRegistered event
     * 
     * @param event The PetRegistered event containing pet registration details
     * @param processingContext The processing context for command handling
     */
    @EventHandler
    fun handle(event: PetRegistered, processingContext: ProcessingContext) {
        logger.info("Processing pet registration notification for external mail system. Pet ID: {}", event.petId)

        // Log the external action to be performed
        logger.info("External System Action: Send Owner Notification - Preparing to send notification email to pet owner")

        // Create and send the NotifyOwner command back to the system
        val notifyCommand = NotifyOwner(
            petId = event.petId,
            ownerId = event.ownerId,
            petName = event.petName,
            ownerEmail = event.ownerEmail
        )

        logger.debug("Sending NotifyOwner command for pet: {} to owner: {}", event.petName, event.ownerEmail)
        commandGateway.send(notifyCommand, processingContext)
    }
}