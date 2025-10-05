package io.axoniq.build.jupiter_wheels.write.user_account_management

import io.axoniq.build.jupiter_wheels.user_account_management.*
import io.axoniq.build.jupiter_wheels.user_account_management.api.*
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
 * Axon Framework test fixture for User Account Management component.
 * Tests command handlers, events, and state evolution using event sourcing.
 */
class UserAccountManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, UserAccountManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("UserAccountManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> UserAccountManagementCommandHandler() }

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
    fun `given no prior activity, when registering account, then user account created`() {
        val registerCommand = RegisterAccount(
            email = "test@example.com",
            phoneNumber = "123456789",
            name = "Test User"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(registerCommand)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as UserRegistrationResult
                assertThat(payload.userId).isNotEmpty()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as UserAccountCreated
                assertThat(event.email).isEqualTo("test@example.com")
                assertThat(event.phoneNumber).isEqualTo("123456789")
                assertThat(event.name).isEqualTo("Test User")
                assertThat(event.userId).isNotEmpty()
            }
    }

    @Test
    fun `given user account created, when verifying email with correct token, then email verified`() {
        val userId = "user123"
        val verificationToken = "token123"

        // Mock the state to have the correct verification token
        val userCreatedEvent = UserAccountCreated(
            userId = userId,
            email = "test@example.com",
            phoneNumber = "123456789",
            name = "Test User"
        )

        fixture.given()
            .event(userCreatedEvent)
            .`when`()
            .command(VerifyEmail(userId = userId, verificationToken = verificationToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as EmailVerificationResult
                assertThat(payload.verificationSuccessful).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as EmailVerified
                assertThat(event.userId).isEqualTo(userId)
                assertThat(event.verificationDate).isNotNull()
            }
    }

    @Test
    fun `given non-existent user, when verifying email, then verification fails`() {
        val userId = "nonexistent"
        val verificationToken = "token123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(VerifyEmail(userId = userId, verificationToken = verificationToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as EmailVerificationResult
                assertThat(payload.verificationSuccessful).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given user with verified email, when verifying email again, then already verified`() {
        val userId = "user123"
        val verificationToken = "token123"
        val userCreatedEvent = UserAccountCreated(
            userId = userId,
            email = "test@example.com",
            phoneNumber = "123456789",
            name = "Test User"
        )
        val emailVerifiedEvent = EmailVerified(
            userId = userId,
            verificationDate = LocalDateTime.now()
        )

        fixture.given()
            .event(userCreatedEvent)
            .event(emailVerifiedEvent)
            .`when`()
            .command(VerifyEmail(userId = userId, verificationToken = verificationToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as EmailVerificationResult
                assertThat(payload.verificationSuccessful).isTrue()
            }
            .noEvents()
    }

    @Test
    fun `given user account created, when verifying email with wrong token, then verification fails`() {
        val userId = "user123"
        val correctToken = "correctToken"
        val wrongToken = "wrongToken"

        val userCreatedEvent = UserAccountCreated(
            userId = userId,
            email = "test@example.com",
            phoneNumber = "123456789",
            name = "Test User"
        )

        fixture.given()
            .event(userCreatedEvent)
            .`when`()
            .command(VerifyEmail(userId = userId, verificationToken = wrongToken))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as EmailVerificationResult
                assertThat(payload.verificationSuccessful).isFalse()
            }
            .noEvents()
    }
}