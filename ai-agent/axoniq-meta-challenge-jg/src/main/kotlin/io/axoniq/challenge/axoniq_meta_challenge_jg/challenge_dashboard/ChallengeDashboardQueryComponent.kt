package io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard

import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_dashboard.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Query component for the Challenge Dashboard.
 * Handles queries for challenge overview and progress dashboard data,
 * and processes events to maintain the read model.
 */
@Component
class ChallengeDashboardQueryComponent(
    private val repository: ChallengeDashboardRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ChallengeDashboardQueryComponent::class.java)
    }

    /**
     * Handles ChallengeOverview query to provide general challenge information.
     * Returns static challenge overview data including requirements and estimated completion time.
     */
    @QueryHandler
    fun handle(query: ChallengeOverview): ChallengeOverviewData {
        logger.info("Processing ChallengeOverview query for Challenge Dashboard component")
        
        return ChallengeOverviewData(
            challengeTitle = "AxonIQ Meta Challenge",
            requirements = listOf(
                "Complete application generation",
                "Share project to gallery",
                "Cast vote for community projects",
                "Meet eligibility requirements"
            ),
            estimatedCompletionTime = 120 // 2 hours in minutes
        )
    }

    /**
     * Handles ProgressDashboard query to provide participant-specific progress information.
     * Returns detailed progress data for the specified participant.
     */
    @QueryHandler
    fun handle(query: ProgressDashboard): ProgressDashboardData {
        logger.info("Processing ProgressDashboard query for participant: ${query.participantId}")

        val entity = repository.findById(query.participantId)
            .orElse(ChallengeDashboardEntity(participantId = query.participantId))

        return ProgressDashboardData(
            completionPercentage = entity.completionPercentage,
            voteCast = entity.voteCast,
            participantId = entity.participantId,
            stepInstructions = entity.stepInstructions,
            applicationCreated = entity.applicationCreated,
            isEligible = entity.isEligible,
            projectShared = entity.projectShared
        )
    }

    /**
     * Handles VoteRegistered event to update participant's vote status.
     * Updates the read model when a participant casts a vote.
     */
    @EventHandler
    fun on(event: VoteRegistered) {
        logger.info("Processing VoteRegistered event for participant: ${event.participantId}")

        val entity = repository.findById(event.participantId)
            .orElse(ChallengeDashboardEntity(participantId = event.participantId))

        val updatedEntity = entity.copy(
            voteCast = true,
            completionPercentage = calculateCompletionPercentage(entity.copy(voteCast = true))
        )

        repository.save(updatedEntity)
        logger.info("Updated vote status for participant: ${event.participantId}")
    }

    /**
     * Handles EligibilityDetermined event to update participant's eligibility status.
     * Updates the read model when a participant's eligibility is determined.
     */
    @EventHandler
    fun on(event: EligibilityDetermined) {
        logger.info("Processing EligibilityDetermined event for participant: ${event.participantId}, eligible: ${event.isEligible}")

        val entity = repository.findById(event.participantId)
            .orElse(ChallengeDashboardEntity(participantId = event.participantId))

        val updatedEntity = entity.copy(
            isEligible = event.isEligible,
            completionPercentage = calculateCompletionPercentage(entity.copy(isEligible = event.isEligible))
        )

        repository.save(updatedEntity)
        logger.info("Updated eligibility status for participant: ${event.participantId}")
    }

    /**
     * Handles ChallengeStarted event to mark the challenge as started for a participant.
     * Updates the read model when a participant starts the challenge.
     */
    @EventHandler
    fun on(event: ChallengeStarted) {
        logger.info("Processing ChallengeStarted event for participant: ${event.participantId}")

        val entity = repository.findById(event.participantId)
            .orElse(ChallengeDashboardEntity(participantId = event.participantId))

        val stepInstructions = listOf(
            "Generate your application using the challenge requirements",
            "Test your application thoroughly",
            "Share your project to the community gallery",
            "Review and vote on other community projects"
        )

        val updatedEntity = entity.copy(
            challengeStarted = true,
            stepInstructions = stepInstructions,
            completionPercentage = calculateCompletionPercentage(entity.copy(challengeStarted = true, stepInstructions = stepInstructions))
        )
        
        repository.save(updatedEntity)
        logger.info("Challenge started for participant: ${event.participantId}")
    }

    /**
     * Handles ProjectSharedToGallery event to update participant's project sharing status.
     * Updates the read model when a participant shares their project.
     */
    @EventHandler
    fun on(event: ProjectSharedToGallery) {
        logger.info("Processing ProjectSharedToGallery event for participant: ${event.participantId}")

        val entity = repository.findById(event.participantId)
            .orElse(ChallengeDashboardEntity(participantId = event.participantId))

        val updatedEntity = entity.copy(
            projectShared = true,
            completionPercentage = calculateCompletionPercentage(entity.copy(projectShared = true))
        )

        repository.save(updatedEntity)
        logger.info("Project shared status updated for participant: ${event.participantId}")
    }

    /**
     * Handles ApplicationGeneratedSuccessfully event to update participant's application creation status.
     * Updates the read model when a participant successfully generates their application.
     */
    @EventHandler
    fun on(event: ApplicationGeneratedSuccessfully) {
        logger.info("Processing ApplicationGeneratedSuccessfully event for participant: ${event.participantId}")

        val entity = repository.findById(event.participantId)
            .orElse(ChallengeDashboardEntity(participantId = event.participantId))
        
        val updatedEntity = entity.copy(
            applicationCreated = true,
            completionPercentage = calculateCompletionPercentage(entity.copy(applicationCreated = true))
        )

        repository.save(updatedEntity)
        logger.info("Application creation status updated for participant: ${event.participantId}")
    }

    /**
     * Calculates the completion percentage based on completed milestones.
     * Each milestone contributes equally to the overall completion percentage.
     */
    private fun calculateCompletionPercentage(entity: ChallengeDashboardEntity): Int {
        var completed = 0
        val totalMilestones = 4 // applicationCreated, projectShared, voteCast, isEligible

        if (entity.applicationCreated) completed++
        if (entity.projectShared) completed++
        if (entity.voteCast) completed++
        if (entity.isEligible) completed++
        
        return (completed * 100) / totalMilestones
    }
}

