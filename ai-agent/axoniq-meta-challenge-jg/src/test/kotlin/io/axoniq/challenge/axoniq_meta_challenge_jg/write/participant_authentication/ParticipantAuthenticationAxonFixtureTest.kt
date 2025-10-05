package io.axoniq.challenge.axoniq_meta_challenge_jg.write.participant_authentication

import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.ParticipantAuthenticationCommandHandler
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.ParticipantAuthenticationState
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.exception.InvalidCredentials
import io.axoniq.challenge.axoniq_meta_challenge_jg.participant_authentication.exception.SessionExpired
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ParticipantAuthenticationAxonFixtureTest {
    
    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, ParticipantAuthenticationState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("ParticipantAuthentication")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> ParticipantAuthenticationCommandHandler() }

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
    fun `given no prior activity, when request password reset, then password reset email sent`() {
        val email = "test@example.com"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RequestPasswordReset(email))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PasswordResetResult
                assertThat(payload.isSuccessful).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PasswordResetEmailSent
                assertThat(event.email).isEqualTo(email)
                assertThat(event.participantId).isNotBlank()
            }
    }

    @Test
    fun `given no prior activity, when create account, then participant authenticated`() {
        val command = CreateAccount(
            password = "password123",
            firstName = "John",
            email = "john@example.com",
            lastName = "Doe"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AccountCreationResult
                assertThat(payload.isSuccessful).isTrue()
                assertThat(payload.participantId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ParticipantAuthenticated
                assertThat(event.email).isEqualTo("john@example.com")
                assertThat(event.authenticationMethod).isEqualTo("credentials")
                assertThat(event.participantId).isNotBlank()
            }
    }
    
    @Test
    fun `given authenticated participant, when create account, then session expired exception`() {
        val participantId = "participant-123"
        
        fixture.given()
            .event(ParticipantAuthenticated(participantId, "existing@example.com", "credentials"))
            .`when`()
            .command(CreateAccount("password", "John", "john@example.com", "Doe"))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(SessionExpired::class.java)
                    .hasMessageContaining("Current session has expired")
            }
    }

    @Test
    fun `given no prior activity, when login with github, then participant authenticated`() {
        val githubToken = "github-token-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(LoginWithGitHub(githubToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ParticipantLoginResult
                assertThat(payload.isSuccessful).isTrue()
                assertThat(payload.participantId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ParticipantAuthenticated
                assertThat(event.authenticationMethod).isEqualTo("github")
                assertThat(event.participantId).contains("github_")
                assertThat(event.email).contains("@github.local")
            }
    }

    @Test
    fun `given authenticated with credentials, when login with github, then session expired exception`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ParticipantAuthenticated(participantId, "user@example.com", "credentials"))
            .`when`()
            .command(LoginWithGitHub("github-token"))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(SessionExpired::class.java)
                    .hasMessageContaining("Current session has expired")
            }
    }

    @Test
    fun `given no prior activity, when login with credentials, then participant authenticated`() {
        val command = LoginWithCredentials("password123", "user@example.com")

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ParticipantLoginResult
                assertThat(payload.isSuccessful).isTrue()
                assertThat(payload.participantId).isNotBlank()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ParticipantAuthenticated
                assertThat(event.email).isEqualTo("user@example.com")
                assertThat(event.authenticationMethod).isEqualTo("credentials")
            }
    }
    
    @Test
    fun `given no prior activity, when login with empty password, then invalid credentials exception`() {
        val command = LoginWithCredentials("", "user@example.com")

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(InvalidCredentials::class.java)
                    .hasMessageContaining("Invalid email or password")
            }
    }

    @Test
    fun `given authenticated with github, when login with credentials, then session expired exception`() {
        val participantId = "github_123"

        fixture.given()
            .event(ParticipantAuthenticated(participantId, "github@example.com", "github"))
            .`when`()
            .command(LoginWithCredentials("password", "user@example.com"))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(SessionExpired::class.java)
                    .hasMessageContaining("Current session has expired")
            }
    }

    @Test
    fun `given participant authenticated, when participant authenticated event, then state updated`() {
        val participantId = "participant-123"
        val email = "user@example.com"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .applying(ParticipantAuthenticated(participantId, email, "credentials"))
            .then()
            .success()
    }

    @Test
    fun `given participant exists, when password reset email sent event, then event processed`() {
        val participantId = "participant-123"

        fixture.given()
            .event(ParticipantAuthenticated(participantId, "user@example.com", "credentials"))
            .`when`()
            .applying(PasswordResetEmailSent(participantId, "user@example.com"))
            .then()
            .success()
    }
}