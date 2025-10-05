package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.exception.ChallengeAlreadyCompleted
import org.axonframework.commandhandling.annotations.CommandHandler
import org.axonframework.eventhandling.gateway.EventAppender
import org.axonframework.modelling.annotations.InjectEntity
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * Challenge Management Command Handler that processes challenge-related commands.
 * This handler manages the lifecycle of participant challenges including initialization,
 * completion checking, and restart prevention.
 */
class ChallengeManagementCommandHandler {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChallengeManagementCommandHandler::class.java)
    }

    /**
     * Handles BeginChallenge command to start a new challenge for a participant
     */
    @CommandHandler
    fun handle(
        command: BeginChallenge,
        @InjectEntity state: ChallengeManagementState,
        eventAppender: EventAppender
    ): ChallengeBeginResult {
        logger.info("Processing BeginChallenge command for participant: ${command.participantId}")

        // Check if challenge is already started
        if (state.getChallengeStatus() != "NOT_STARTED") {
            logger.warn("Challenge already started for participant: ${command.participantId}")
            return ChallengeBeginResult(isSuccessful = false)
        }

        // Start the challenge
        val event = ChallengeStarted(participantId = command.participantId)
        eventAppender.append(event)

        logger.info("Challenge started successfully for participant: ${command.participantId}")
        return ChallengeBeginResult(isSuccessful = true)
    }

    /**
     * Handles CheckChallengeCompletion command to verify participant eligibility and completion status
     */
    @CommandHandler
    fun handle(
        command: CheckChallengeCompletion,
        @InjectEntity state: ChallengeManagementState,
        eventAppender: EventAppender
    ): ChallengeCompletionResult {
        logger.info("Processing CheckChallengeCompletion command for participant: ${command.participantId}")

        // Calculate completion percentage based on completed tasks
        var completedTasks = 0
        val totalTasks = 4 // voteCast, applicationCreated, projectShared, and challenge started

        if (state.getChallengeStatus() == "STARTED" || state.getChallengeStatus() == "COMPLETED") {
            completedTasks++
        }
        if (state.getVoteCast()) {
            completedTasks++
        }
        if (state.getApplicationCreated()) {
            completedTasks++
        }
        if (state.getProjectShared()) {
            completedTasks++
        }

        val completionPercentage = (completedTasks * 100) / totalTasks

        // Determine eligibility (all tasks must be completed)
        val isEligible = completedTasks == totalTasks

        if (isEligible) {
            // Emit eligibility determined event
            val eligibilityEvent = EligibilityDetermined(
                participantId = command.participantId,
                isEligible = true
            )
            eventAppender.append(eligibilityEvent)
            logger.info("Participant ${command.participantId} is eligible for prize")
        } else {
            // Emit ineligible event
            val ineligibleEvent = ParticipantIneligibleForPrize(
                participantId = command.participantId
            )
            eventAppender.append(ineligibleEvent)
            logger.info("Participant ${command.participantId} is not eligible for prize")
        }

        return ChallengeCompletionResult(
            completionPercentage = completionPercentage,
            isEligible = isEligible
        )
    }

    /**
     * Handles AttemptChallengeRestart command to prevent restart of completed challenges
     */
    @CommandHandler
    fun handle(
        command: AttemptChallengeRestart,
        @InjectEntity state: ChallengeManagementState,
        eventAppender: EventAppender
    ): ChallengeRestartResult {
        logger.info("Processing AttemptChallengeRestart command for participant: ${command.participantId}")

        // Check if challenge is already completed
        if (state.getChallengeStatus() == "COMPLETED" || state.getIsEligibleForPrize()) {
            logger.warn("Challenge restart attempted for completed challenge, participant: ${command.participantId}")
            throw ChallengeAlreadyCompleted("Challenge has already been completed and cannot be restarted")
        }

        // Challenge can be restarted
        logger.info("Challenge restart allowed for participant: ${command.participantId}")
        return ChallengeRestartResult(isAllowed = true)
    }
}

