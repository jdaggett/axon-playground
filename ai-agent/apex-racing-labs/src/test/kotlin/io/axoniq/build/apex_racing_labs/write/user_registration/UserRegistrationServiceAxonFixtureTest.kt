package io.axoniq.build.apex_racing_labs.write.user_registration

import io.axoniq.build.apex_racing_labs.user_registration.UserRegistrationServiceCommandHandler
import io.axoniq.build.apex_racing_labs.user_registration.UserRegistrationState
import io.axoniq.build.apex_racing_labs.user_registration.api.*
import io.axoniq.build.apex_racing_labs.user_registration.exception.EmailAlreadyRegistered
import io.axoniq.build.apex_racing_labs.user_registration.exception.VerificationTokenExpired
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock

/**
 * Test class for the User Registration Service component using Axon Test Fixture.
 * Tests command handling, event sourcing, and exception scenarios.
 */
class UserRegistrationServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        val commandGateway = mock(CommandGateway::class.java)
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, UserRegistrationState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("UserRegistrationService")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> UserRegistrationServiceCommandHandler(commandGateway) }

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
    fun `given no prior activity, when create account, then account created event published`() {
        val email = "test@example.com"
        val password = "password123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateAccount(password = password, email = email))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as AccountCreated
                assertThat(event.email).isEqualTo(email)
                assertThat(event.verificationToken).isNotEmpty()
            }
    }

    @Test
    fun `given account already created, when create account with same email, then exception thrown`() {
        val email = "test@example.com"
        val verificationToken = "token-123"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .`when`()
            .command(CreateAccount(password = "password123", email = email))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(EmailAlreadyRegistered::class.java)
                    .hasMessageContaining("An account with this email address already exists")
            }
    }

    @Test
    fun `given account created, when verify email with correct token, then email verified event published`() {
        val email = "test@example.com"
        val verificationToken = "token-123"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .`when`()
            .command(VerifyEmail(verificationToken = verificationToken))
            .then()
            .success()
            .events(EmailVerified(email = email))
    }

    @Test
    fun `given account created, when verify email with incorrect token, then exception thrown`() {
        val email = "test@example.com"
        val verificationToken = "token-123"
        val incorrectToken = "wrong-token"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .`when`()
            .command(VerifyEmail(verificationToken = incorrectToken))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(VerificationTokenExpired::class.java)
                    .hasMessageContaining("Invalid or expired verification token")
            }
    }

    @Test
    fun `given no account exists, when verify email, then exception thrown`() {
        val verificationToken = "token-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(VerifyEmail(verificationToken = verificationToken))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(VerificationTokenExpired::class.java)
                    .hasMessageContaining("Invalid verification token")
            }
    }

    @Test
    fun `given account marked unverified, when verify email, then exception thrown`() {
        val email = "test@example.com"
        val verificationToken = "token-123"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .event(AccountMarkedUnverified(email = email))
            .`when`()
            .command(VerifyEmail(verificationToken = verificationToken))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(VerificationTokenExpired::class.java)
                    .hasMessageContaining("Verification token has expired")
            }
    }

    @Test
    fun `given email already verified, when verify email again, then success returned`() {
        val email = "test@example.com"
        val verificationToken = "token-123"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .event(EmailVerified(email = email))
            .`when`()
            .command(VerifyEmail(verificationToken = verificationToken))
            .then()
            .success()
            .noEvents()
    }

    @Test
    fun `given unverified account, when email verification deadline reached, then account marked unverified`() {
        val email = "test@example.com"
        val verificationToken = "token-123"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .`when`()
            .command(EmailVerificationDeadlineReached())
            .then()
            .success()
            .events(AccountMarkedUnverified(email = email))
    }

    @Test
    fun `given already verified account, when email verification deadline reached, then no events published`() {
        val email = "test@example.com"
        val verificationToken = "token-123"

        fixture.given()
            .event(AccountCreated(email = email, verificationToken = verificationToken))
            .event(EmailVerified(email = email))
            .`when`()
            .command(EmailVerificationDeadlineReached())
            .then()
            .success()
            .noEvents()
    }

    @Test
    fun `given no account, when email verification deadline reached, then no events published`() {
        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(EmailVerificationDeadlineReached())
            .then()
            .success()
            .noEvents()
    }
}