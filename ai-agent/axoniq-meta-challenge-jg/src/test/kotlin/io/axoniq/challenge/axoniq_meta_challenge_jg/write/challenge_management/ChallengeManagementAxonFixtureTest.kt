package io.axoniq.challenge.axoniq_meta_challenge_jg.write.challenge_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.ChallengeManagementCommandHandler
import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.ChallengeManagementState
import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.challenge_management.exception.ChallengeAlreadyCompleted
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Axon Test Fixture tests for Challenge Management component.
 * Tests command handling scenarios using event sourcing.
 */
class ChallengeManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, ChallengeManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("ChallengeManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> ChallengeManagementCommandHandler() }

        configurer = configurer.registerEntity(stateEntity)
            .registerCommandHandlingModule(commandHandlingModule)
            .componentRegistry { cr -> cr.disableEnhancer(AxonServerConfigurationEnhancer::class.java) }
        fixture = AxonTestFixture.with(configurer)
    }

    @AfterEach
    fun afterEach() {
        fixture.stop()
    }

    @Test
    fun `given no prior activity, when begin challenge, then challenge started`() {
        val participantId = "participant-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(BeginChallenge(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeBeginResult
                assertThat(payload.isSuccessful).isTrue()
            }
            .events(ChallengeStarted(participantId))
    }

    @Test
    fun `given challenge already started, when begin challenge, then unsuccessful result`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .`when`()
            .command(BeginChallenge(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeBeginResult
                assertThat(payload.isSuccessful).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given challenge started, when check completion, then partial completion and ineligible`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .`when`()
            .command(CheckChallengeCompletion(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeCompletionResult
                assertThat(payload.completionPercentage).isEqualTo(25) // 1 of 4 tasks completed
                assertThat(payload.isEligible).isFalse()
            }
            .events(ParticipantIneligibleForPrize(participantId))
    }

    @Test
    fun `given all tasks completed, when check completion, then full completion and eligible`() {
        val participantId = "participant-123"
        val applicationId = "app-123"
        val projectId = "project-123"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .event(VoteRegistered(participantId, "upvote", "some-project"))
            .event(ApplicationGeneratedSuccessfully(applicationId, participantId))
            .event(ProjectSharedToGallery(LocalDateTime.now(), participantId, "My Project", projectId))
            .`when`()
            .command(CheckChallengeCompletion(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeCompletionResult
                assertThat(payload.completionPercentage).isEqualTo(100) // All tasks completed
                assertThat(payload.isEligible).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as EligibilityDetermined
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.isEligible).isTrue()
            }
    }

    @Test
    fun `given challenge not completed, when attempt restart, then restart allowed`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .`when`()
            .command(AttemptChallengeRestart(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeRestartResult
                assertThat(payload.isAllowed).isTrue()
            }
            .noEvents()
    }

    @Test
    fun `given challenge completed, when attempt restart, then exception thrown`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .event(EligibilityDetermined(participantId, true))
            .`when`()
            .command(AttemptChallengeRestart(participantId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(ChallengeAlreadyCompleted::class.java)
                    .hasMessageContaining("Challenge has already been completed and cannot be restarted")
            }
    }

    @Test
    fun `given partial completion with vote cast, when check completion, then higher percentage`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .event(VoteRegistered(participantId, "upvote", "project-456"))
            .`when`()
            .command(CheckChallengeCompletion(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeCompletionResult
                assertThat(payload.completionPercentage).isEqualTo(50) // 2 of 4 tasks completed
                assertThat(payload.isEligible).isFalse()
            }
            .events(ParticipantIneligibleForPrize(participantId))
    }

    @Test
    fun `given no prior activity, when check completion, then zero completion`() {
        val participantId = "participant-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CheckChallengeCompletion(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeCompletionResult
                assertThat(payload.completionPercentage).isEqualTo(0)
                assertThat(payload.isEligible).isFalse()
            }
            .events(ParticipantIneligibleForPrize(participantId))
    }

    @Test
    fun `given application created, when check completion, then reflect application task`() {
        val participantId = "participant-123"
        val applicationId = "app-456"

        fixture.given()
            .event(ChallengeStarted(participantId))
            .event(ApplicationGeneratedSuccessfully(applicationId, participantId))
            .`when`()
            .command(CheckChallengeCompletion(participantId))
            .then()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ChallengeCompletionResult
                assertThat(payload.completionPercentage).isEqualTo(50) // 2 of 4 tasks completed
                assertThat(payload.isEligible).isFalse()
            }
            .events(ParticipantIneligibleForPrize(participantId))
    }
}