package io.axoniq.build.sleep_on_time.xesar_door_control

import io.axoniq.build.sleep_on_time.xesar_door_control.api.DoorOpeningRequested
import io.axoniq.build.sleep_on_time.xesar_door_control.api.ConfirmDoorUnlocked
import org.springframework.stereotype.Service
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.messaging.unitofwork.ProcessingContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Xesar Door Control external system component
 * Handles container door unlock operations with Xesar system
 * 
 * This component processes door opening requests and communicates with the Xesar system
 * to perform actual door unlock operations for guest access to sleeping containers.
 */
@Service
class XesarDoorControlComponent(
    private val commandGateway: CommandGateway
) {

    private val logger: Logger = LoggerFactory.getLogger(XesarDoorControlComponent::class.java)

    /**
     * Handles door opening requests by communicating with the Xesar system
     * 
     * This method processes DoorOpeningRequested events and performs the XesarUnlockContainer action
     * by interfacing with the external Xesar door control system to unlock the specified container
     * for the requesting guest.
     * 
     * @param event The DoorOpeningRequested event containing booking, guest, and container information
     * @param processingContext The processing context for command handling
     */
    @EventHandler
    fun handleDoorOpeningRequested(event: DoorOpeningRequested, processingContext: ProcessingContext) {
        logger.info("Handling Xesar door unlock request for container ${event.containerId} " +
                   "for guest ${event.guestId} with booking ${event.bookingId}")

        // Log the external system action that would be performed
        logger.info("XesarUnlockContainer action: Unlocking container ${event.containerId} " +
                   "for guest ${event.guestId} at ${event.timestamp}")
        
        // In a real implementation, this would communicate with the Xesar API
        // For now, we simulate the door unlock operation and send confirmation back

        val confirmCommand = ConfirmDoorUnlocked(
            bookingId = event.bookingId,
            guestId = event.guestId,
            unlockTimestamp = LocalDateTime.now(),
            containerId = event.containerId
        )

        logger.debug("Sending ConfirmDoorUnlocked command back to system for booking ${event.bookingId}")
        commandGateway.send(confirmCommand, processingContext)
    }
}