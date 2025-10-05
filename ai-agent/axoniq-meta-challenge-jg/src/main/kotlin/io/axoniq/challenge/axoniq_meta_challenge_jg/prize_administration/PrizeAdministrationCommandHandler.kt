package io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration

import io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api.*
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Command handler for Prize Administration component.
 * Handles prize winner selection, announcements, and prize claims.
 */
class PrizeAdministrationCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PrizeAdministrationCommandHandler::class.java)
    }

    /**
     * Handles the SelectPrizeWinners command for Prize Administration component.
     * Selects prize winners and publishes WinnersSelected event.
     */
    @CommandHandler
    fun handle(
        command: SelectPrizeWinners,
        @InjectEntity state: PrizeAdministrationState,
        eventAppender: EventAppender
    ): WinnerSelectionResult {
        logger.info("Handling SelectPrizeWinners command for employee: ${command.employeeId}")

        // Validate that there are winners to select
        if (command.selectedWinnerIds.isEmpty()) {
            throw IllegalArgumentException("No winners selected")
        }

        // Create and append the WinnersSelected event
        val event = WinnersSelected(
            selectionTime = LocalDateTime.now(),
            winnerIds = command.selectedWinnerIds
        )
        eventAppender.append(event)

        logger.info("Winners selected successfully, count: ${command.selectedWinnerIds.size}")
        return WinnerSelectionResult(selectedCount = command.selectedWinnerIds.size)
    }

    /**
     * Handles the AnnounceSelectedPrizeWinners command for Prize Administration component.
     * Announces the selected prize winners and publishes PrizesAnnounced event.
     */
    @CommandHandler
    fun handle(
        command: AnnounceSelectedPrizeWinners,
        @InjectEntity state: PrizeAdministrationState,
        eventAppender: EventAppender
    ): PrizeAnnouncementResult {
        logger.info("Handling AnnounceSelectedPrizeWinners command")

        // Validate that winners have been selected before announcement
        if (state.getWinnerIds().isEmpty()) {
            throw IllegalStateException("No winners selected yet, cannot announce")
        }

        // Validate that announcement hasn't already been made
        if (state.getAnnouncementMade()) {
            throw IllegalStateException("Prize winners have already been announced")
        }

        // Create and append the PrizesAnnounced event
        val event = PrizesAnnounced(
            announcementTime = LocalDateTime.now()
        )
        eventAppender.append(event)

        logger.info("Prize winners announced successfully")
        return PrizeAnnouncementResult(isSuccessful = true)
    }

    /**
     * Handles the ClaimPrize command for Prize Administration component.
     * Processes prize claims and publishes PrizeClaimed event.
     */
    @CommandHandler
    fun handle(
        command: ClaimPrize,
        @InjectEntity state: PrizeAdministrationState,
        eventAppender: EventAppender
    ): PrizeClaimResult {
        logger.info("Handling ClaimPrize command for participant: ${command.participantId}, prize: ${command.prizeId}")

        // Validate that participant is a winner
        if (!state.getWinnerIds().contains(command.participantId)) {
            throw IllegalStateException("Participant ${command.participantId} is not a selected winner")
        }

        // Validate that announcement has been made
        if (!state.getAnnouncementMade()) {
            throw IllegalStateException("Prize winners have not been announced yet")
        }

        // Validate that prize hasn't already been claimed
        if (state.getClaimedPrizes().contains(command.prizeId)) {
            throw IllegalStateException("Prize ${command.prizeId} has already been claimed")
        }

        // Create and append the PrizeClaimed event
        val event = PrizeClaimed(
            participantId = command.participantId,
            prizeId = command.prizeId,
            claimTime = LocalDateTime.now()
        )
        eventAppender.append(event)

        logger.info("Prize claimed successfully by participant: ${command.participantId}")
        return PrizeClaimResult(isSuccessful = true)
    }
}

