package io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration

import io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api.*
import org.axonframework.eventsourcing.annotations.EventSourcingHandler
import org.axonframework.eventsourcing.annotations.reflection.EntityCreator
import org.axonframework.eventsourcing.annotations.EventSourcedEntity

/**
 * Event-sourced entity representing the state of Prize Administration.
 * Maintains the state of selected winners, claimed prizes, and announcement status.
 */
@EventSourcedEntity
class PrizeAdministrationState {

    private var winnerIds: MutableList<String> = mutableListOf()
    private var claimedPrizes: MutableList<String> = mutableListOf()
    private var announcementMade: Boolean = false

    /**
     * Gets the list of selected winner IDs.
     */
    fun getWinnerIds(): List<String> = winnerIds.toList()

    /**
     * Gets the list of claimed prize IDs.
     */
    fun getClaimedPrizes(): List<String> = claimedPrizes.toList()

    /**
     * Gets whether the announcement has been made.
     */
    fun getAnnouncementMade(): Boolean = announcementMade

    @EntityCreator
    constructor()

    /**
     * Event sourcing handler for WinnersSelected event.
     * Updates the state with the selected winner IDs.
     */
    @EventSourcingHandler
    fun evolve(event: WinnersSelected) {
        this.winnerIds = event.winnerIds.toMutableList()
    }

    /**
     * Event sourcing handler for PrizesAnnounced event.
     * Marks that the announcement has been made.
     */
    @EventSourcingHandler
    @Suppress("UNUSED_PARAMETER")
    fun evolve(event: PrizesAnnounced) {
        this.announcementMade = true
    }

    /**
     * Event sourcing handler for PrizeClaimed event.
     * Adds the claimed prize to the list of claimed prizes.
     */
    @EventSourcingHandler
    fun evolve(event: PrizeClaimed) {
        this.claimedPrizes.add(event.prizeId)
    }
}