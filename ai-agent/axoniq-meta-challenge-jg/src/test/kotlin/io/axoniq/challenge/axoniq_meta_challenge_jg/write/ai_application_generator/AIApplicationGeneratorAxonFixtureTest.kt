package io.axoniq.challenge.axoniq_meta_challenge_jg.write.ai_application_generator

import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.AIApplicationGeneratorCommandHandler
import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.AIApplicationGeneratorState
import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.ai_application_generator.exception.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Tests for the AI Application Generator command component using Axon Framework test fixture.
 * Verifies command handling, event generation, and exception scenarios.
 */
class AIApplicationGeneratorAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, AIApplicationGeneratorState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("AIApplicationGenerator")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> AIApplicationGeneratorCommandHandler() }

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
    fun `given no prior activity, when generate AI application, then AI generation started event is emitted`() {
        val participantId = "participant-123"
        val applicationParameters = "basic-web-app"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(GenerateAIApplication(participantId, applicationParameters))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ApplicationGenerationResult
                assertThat(payload.isSuccessful).isTrue()
                assertThat(payload.applicationId).isNotNull()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as AIGenerationStarted
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.applicationParameters).isEqualTo(applicationParameters)
            }
    }

    @Test
    fun `given no prior activity, when generate AI application with FORCE_FAIL parameter, then AI generation failed exception is thrown`() {
        val participantId = "participant-123"
        val applicationParameters = "FORCE_FAIL"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(GenerateAIApplication(participantId, applicationParameters))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(AIGenerationFailed::class.java)
                    .hasMessageContaining("AI generation failed for participant: $participantId")
            }
    }

    @Test
    fun `given no prior activity, when generate AI application with FORCE_PARTIAL parameter, then AI generation partially failed exception is thrown`() {
        val participantId = "participant-123"
        val applicationParameters = "FORCE_PARTIAL"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(GenerateAIApplication(participantId, applicationParameters))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(AIGenerationPartiallyFailed::class.java)
                    .hasMessageContaining("AI generation partially failed for participant: $participantId")
            }
    }

    @Test
    fun `given AI generation started, when generate AI application again, then AI generation failed exception is thrown`() {
        val participantId = "participant-123"
        val applicationParameters = "basic-web-app"

        fixture.given()
            .event(AIGenerationStarted(participantId, applicationParameters))
            .`when`()
            .command(GenerateAIApplication(participantId, applicationParameters))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(AIGenerationFailed::class.java)
                    .hasMessageContaining("AI generation is already in progress for participant: $participantId")
            }
    }

    @Test
    fun `given AI generation started, when retry AI generation, then AI generation retried event is emitted`() {
        val participantId = "participant-123"
        val originalParameters = "basic-web-app"

        fixture.given()
            .event(AIGenerationStarted(participantId, originalParameters))
            .`when`()
            .command(RetryAIGeneration(participantId, originalParameters))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ApplicationGenerationResult
                assertThat(payload.isSuccessful).isTrue()
                assertThat(payload.applicationId).isNotNull()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as AIGenerationRetried
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.retryAttempt).isEqualTo(1)
            }
    }

    @Test
    fun `given maximum retries exceeded, when retry AI generation, then unsuccessful result is returned`() {
        val participantId = "participant-123"
        val originalParameters = "basic-web-app"

        fixture.given()
            .event(AIGenerationStarted(participantId, originalParameters))
            .event(AIGenerationRetried(1, participantId))
            .event(AIGenerationRetried(2, participantId))
            .event(AIGenerationRetried(3, participantId))
            .`when`()
            .command(RetryAIGeneration(participantId, originalParameters))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ApplicationGenerationResult
                assertThat(payload.isSuccessful).isFalse()
                assertThat(payload.applicationId).isNull()
            }
            .noEvents()
    }

    @Test
    fun `given no prior activity, when resume application work with valid session, then application work resumed event is emitted`() {
        val participantId = "participant-123"
        val sessionToken = "valid-session-token-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ResumeApplicationWork(participantId, sessionToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ApplicationResumeResult
                assertThat(payload.isSuccessful).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ApplicationWorkResumed
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.applicationId).isNotNull()
            }
    }

    @Test
    fun `given no prior activity, when resume application work with invalid session token, then browser session lost exception is thrown`() {
        val participantId = "participant-123"
        val sessionToken = "short"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ResumeApplicationWork(participantId, sessionToken))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(BrowserSessionLost::class.java)
                    .hasMessageContaining("Invalid or expired session token for participant: $participantId")
            }
    }

    @Test
    fun `given application generated successfully, when resume application work, then unsuccessful result is returned`() {
        val participantId = "participant-123"
        val applicationId = "app-123"
        val sessionToken = "valid-session-token-123"

        fixture.given()
            .event(ApplicationGeneratedSuccessfully(applicationId, participantId))
            .`when`()
            .command(ResumeApplicationWork(participantId, sessionToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ApplicationResumeResult
                assertThat(payload.isSuccessful).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given no prior activity, when report successful generation completion, then application generated successfully event is emitted`() {
        val participantId = "participant-123"
        val applicationId = "app-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ReportGenerationCompletion(applicationId, participantId, true))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as GenerationCompletionResult
                assertThat(payload.isSuccessful).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ApplicationGeneratedSuccessfully
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.applicationId).isEqualTo(applicationId)
            }
    }

    @Test
    fun `given no prior activity, when report unsuccessful generation completion, then partial application created event is emitted`() {
        val participantId = "participant-123"
        val applicationId = "app-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ReportGenerationCompletion(applicationId, participantId, false))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as GenerationCompletionResult
                assertThat(payload.isSuccessful).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PartialApplicationCreated
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.applicationId).isEqualTo(applicationId)
            }
    }
}