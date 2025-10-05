package io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard

import io.axoniq.challenge.axoniq_meta_challenge_jg.admin_dashboard.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Query component for Admin Dashboard - handles administrative queries and events
 * for AxonIQ employees to review challenges, participants, and prizes
 */
@Component
class AdminDashboardQueryComponent(
    private val repository: AdminDashboardRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AdminDashboardQueryComponent::class.java)
    }

    /**
     * Query handler for PrizeCollectionInstructions - returns prize collection instructions
     * for a specific participant
     */
    @QueryHandler
    fun handle(query: PrizeCollectionInstructions): PrizeInstructionsData {
        logger.info("Handling PrizeCollectionInstructions query for participant: ${query.participantId}")
        
        // For this implementation, returning static prize collection instructions
        // In a real scenario, this would be fetched from a configuration or database
        return PrizeInstructionsData(
            requiredIdentification = "Government-issued photo ID",
            boothLocation = "AxonIQ Booth #42, Main Conference Hall",
            availableHours = "9:00 AM - 5:00 PM, Monday-Friday"
        )
    }

    /**
     * Query handler for DetailedChallengeCompletion - returns detailed completion data
     * for a specific participant
     */
    @QueryHandler
    fun handle(query: DetailedChallengeCompletion): DetailedCompletionData {
        logger.info("Handling DetailedChallengeCompletion query for participant: ${query.participantId}")
        
        val participant = repository.findById(query.participantId)
            .orElseThrow { IllegalArgumentException("Participant not found: ${query.participantId}") }

        return DetailedCompletionData(
            participantEmail = participant.participantEmail,
            voteCast = true, // Default assumption - would be tracked separately in real implementation
            participantId = participant.participantId,
            applicationCreated = true, // Default assumption - would be tracked separately
            projectShared = true, // Default assumption - would be tracked separately
            completionTime = participant.completionTime ?: LocalDateTime.now()
        )
    }

    /**
     * Query handler for AllRunningChallenges - returns all currently running challenges
     */
    @QueryHandler
    fun handle(query: AllRunningChallenges): AllRunningChallengesData {
        logger.info("Handling AllRunningChallenges query")

        // For this implementation, returning empty list of challenge statuses
        // In a real scenario, this would aggregate challenge data
        val runningChallenges = repository.findRunningChallengeIds().map { 
            ChallengeStatus()
        }

        return AllRunningChallengesData(runningChallenges = runningChallenges)
    }

    /**
     * Query handler for ParticipantResultsDashboard - returns participant results data
     * for eligible participants
     */
    @QueryHandler
    fun handle(query: ParticipantResultsDashboard): ParticipantResultsData {
        logger.info("Handling ParticipantResultsDashboard query")

        val eligibleParticipants = repository.findByIsEligibleTrueOrderByCompletionTimeAsc()
            .map { participant ->
                ParticipantResult(
                    participantEmail = participant.participantEmail,
                    participantId = participant.participantId,
                    completionTime = participant.completionTime ?: LocalDateTime.now()
                )
            }

        return ParticipantResultsData(eligibleParticipants = eligibleParticipants)
    }

    /**
     * Event handler for EligibilityDetermined - updates participant eligibility status
     */
    @EventHandler
    fun on(event: EligibilityDetermined) {
        logger.info("Handling EligibilityDetermined event for participant: ${event.participantId}")

        val participant = repository.findById(event.participantId)
        if (participant.isPresent) {
            val updated = participant.get().copy(isEligible = event.isEligible)
            repository.save(updated)
        } else {
            logger.warn("Participant not found for eligibility update: ${event.participantId}")
        }
    }

    /**
     * Event handler for WinnersSelected - updates winner status for selected participants
     */
    @EventHandler
    fun on(event: WinnersSelected) {
        logger.info("Handling WinnersSelected event for ${event.winnerIds.size} winners")

        event.winnerIds.forEach { winnerId ->
            val participant = repository.findById(winnerId)
            if (participant.isPresent) {
                val updated = participant.get().copy(isWinner = true)
                repository.save(updated)
            } else {
                logger.warn("Participant not found for winner update: $winnerId")
            }
        }
    }

    /**
     * Event handler for ChallengeStarted - creates or updates participant record when challenge starts
     */
    @EventHandler
    fun on(event: ChallengeStarted) {
        logger.info("Handling ChallengeStarted event for participant: ${event.participantId}")

        val existingParticipant = repository.findById(event.participantId)
        if (existingParticipant.isPresent) {
            val updated = existingParticipant.get().copy(
                challengeStatus = "RUNNING",
                startTime = LocalDateTime.now()
            )
            repository.save(updated)
        } else {
            // Create new participant record
            val newParticipant = AdminDashboardEntity(
                participantId = event.participantId,
                challengeId = "default-challenge", // Would be provided in real implementation
                challengeStatus = "RUNNING",
                participantEmail = "unknown@example.com", // Would be fetched from participant service
                startTime = LocalDateTime.now()
            )
            repository.save(newParticipant)
        }
    }

    /**
     * Event handler for PrizesAnnounced - logs prize announcement
     */
    @EventHandler
    fun on(event: PrizesAnnounced) {
        logger.info("Handling PrizesAnnounced event at: ${event.announcementTime}")
        // This event is mainly for logging and notification purposes
        // No specific data model updates needed for this event
    }

    /**
     * Event handler for PrizeClaimed - updates prize claimed status for participant
     */
    @EventHandler
    fun on(event: PrizeClaimed) {
        logger.info("Handling PrizeClaimed event for participant: ${event.participantId}")

        val participant = repository.findById(event.participantId)
        if (participant.isPresent) {
            val updated = participant.get().copy(prizeClaimed = true)
            repository.save(updated)
        } else {
            logger.warn("Participant not found for prize claim update: ${event.participantId}")
        }
    }
}

