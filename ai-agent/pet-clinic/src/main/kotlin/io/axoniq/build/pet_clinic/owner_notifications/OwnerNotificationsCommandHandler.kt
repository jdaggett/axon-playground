package io.axoniq.build.pet_clinic.owner_notifications

import io.axoniq.build.pet_clinic.owner_notifications.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Owner Notifications - Command handler for processing owner notification commands
 * This class handles commands related to owner notification processes
 */
class OwnerNotificationsCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(OwnerNotificationsCommandHandler::class.java)
    }

    /**
     * Owner Notifications - Handles NotifyOwner command
     * Processes the notification request and publishes OwnerNotified event
     */
    @CommandHandler
    fun handle(
        command: NotifyOwner,
        @InjectEntity state: OwnerNotificationsState,
        eventAppender: EventAppender
    ) {
        logger.info("Processing NotifyOwner command for pet: ${command.petId}")
        
        // Create and append the OwnerNotified event
        val event = OwnerNotified(
            ownerEmail = command.ownerEmail,
            petId = command.petId,
            notificationStatus = "SENT"
        )
        
        eventAppender.append(event)
        logger.info("OwnerNotified event appended for pet: ${command.petId}")
    }
}

