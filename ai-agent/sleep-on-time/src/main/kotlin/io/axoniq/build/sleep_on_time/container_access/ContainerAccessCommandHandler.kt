package io.axoniq.build.sleep_on_time.container_access

import io.axoniq.build.sleep_on_time.container_access.api.*
import io.axoniq.build.sleep_on_time.container_access.exception.ContainerAlreadyOccupied
import io.axoniq.build.sleep_on_time.container_access.exception.GuestAlreadyCheckedOut
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Container Access Command Handler - handles container access related commands
 * for the Container Access Management component
 */
class ContainerAccessCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ContainerAccessCommandHandler::class.java)
    }

    /**
     * Handles OpenContainerDoor command - processes request to open container door
     * Validates guest status and container availability before requesting door opening
     */
    @CommandHandler
    fun handle(
        command: OpenContainerDoor,
        @InjectEntity state: ContainerAccessState,
        eventAppender: EventAppender
    ): ContainerDoorOpenResult {
        logger.info("Handling OpenContainerDoor command for guest ${command.guestId}, container ${command.containerId}")

        // Validate guest is not already checked out
        if (state.getStatus() == "AVAILABLE" && state.getCheckedOutAt() != null) {
            throw GuestAlreadyCheckedOut("Guest ${command.guestId} has already checked out from container ${command.containerId}")
        }

        // Request door opening
        val event = DoorOpeningRequested(
            bookingId = command.bookingId,
            guestId = command.guestId,
            timestamp = LocalDateTime.now(),
            containerId = command.containerId
        )
        eventAppender.append(event)

        logger.info("Door opening requested for container ${command.containerId}")
        return ContainerDoorOpenResult(success = true, unlockRequested = true)
    }

    /**
     * Handles ConfirmDoorUnlocked command - confirms that container door has been unlocked
     * Processes guest check-in after door unlock confirmation
     */
    @CommandHandler
    fun handle(
        command: ConfirmDoorUnlocked,
        @InjectEntity state: ContainerAccessState,
        eventAppender: EventAppender
    ): DoorUnlockConfirmationResult {
        logger.info("Handling ConfirmDoorUnlocked command for guest ${command.guestId}, container ${command.containerId}")

        // Check in guest after door unlock confirmation
        val event = GuestCheckedIn(
            checkedInAt = command.unlockTimestamp,
            bookingId = command.bookingId,
            guestId = command.guestId,
            containerId = command.containerId
        )
        eventAppender.append(event)

        logger.info("Guest ${command.guestId} checked in to container ${command.containerId}")
        return DoorUnlockConfirmationResult(success = true)
    }

    /**
     * Handles ObtainContainer command - processes request to obtain/reserve a container
     * Validates container availability before allowing guest to obtain it
     */
    @CommandHandler
    fun handle(
        command: ObtainContainer,
        @InjectEntity state: ContainerAccessState,
        eventAppender: EventAppender
    ): ContainerObtainResult {
        logger.info("Handling ObtainContainer command for guest ${command.guestId}, container ${command.containerId}")
        
        // Validate container is not already occupied
        if (state.getStatus() == "OCCUPIED" || state.getStatus() == "RESERVED") {
            throw ContainerAlreadyOccupied("Container ${command.containerId} is already occupied or reserved")
        }
        
        // Obtain container
        val event = ContainerObtained(
            bookingId = command.bookingId,
            guestId = command.guestId,
            timestamp = LocalDateTime.now(),
            containerId = command.containerId
        )
        eventAppender.append(event)

        logger.info("Container ${command.containerId} obtained by guest ${command.guestId}")
        return ContainerObtainResult(success = true, accessKeyRequested = true)
    }
}

