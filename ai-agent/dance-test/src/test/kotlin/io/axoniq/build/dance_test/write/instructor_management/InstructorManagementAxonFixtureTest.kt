package io.axoniq.build.dance_test.write.instructor_management

import io.axoniq.build.dance_test.instructor_management.*
import io.axoniq.build.dance_test.instructor_management.api.*
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat

/**
 * InstructorManagementAxonFixtureTest - Tests for Instructor Management component using Axon Test Fixture.
 *
 * Verifies command handling, event sourcing, and business logic for instructor profile management
 * and Calendly integration functionality.
 */
class InstructorManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, InstructorManagementState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("InstructorManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> InstructorManagementCommandHandler() }

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
    fun `given no prior activity, when create instructor profile, then success`() {
        val instructorId = "instructor-123"
        val email = "instructor@dance.com"
        val phone = "+1234567890"
        val specialties = listOf("Ballet", "Jazz", "Hip Hop")

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateInstructorProfile(
                instructorId = instructorId,
                email = email,
                phone = phone,
                specialties = specialties
            ))
            .then()
            .success()
            .events(InstructorProfileCreated(
                instructorId = instructorId,
                email = email,
                phone = phone,
                specialties = specialties
            ))
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(InstructorProfileResult::class.java)
                val profileResult = result.payload() as InstructorProfileResult
                assertThat(profileResult.success).isTrue()
                assertThat(profileResult.instructorId).isEqualTo(instructorId)
            }
    }

    @Test
    fun `given instructor profile exists, when create instructor profile again, then failure`() {
        val instructorId = "instructor-123"
        val email = "instructor@dance.com"
        val phone = "+1234567890"
        val specialties = listOf("Ballet", "Jazz")

        fixture.given()
            .event(InstructorProfileCreated(
                instructorId = instructorId,
                email = email,
                phone = phone,
                specialties = specialties
            ))
            .`when`()
            .command(CreateInstructorProfile(
                instructorId = instructorId,
                email = "new@email.com",
                phone = "+9876543210",
                specialties = listOf("Contemporary")
            ))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(InstructorProfileResult::class.java)
                val profileResult = result.payload() as InstructorProfileResult
                assertThat(profileResult.success).isFalse()
                assertThat(profileResult.instructorId).isEqualTo(instructorId)
            }
    }

    @Test
    fun `given instructor profile exists, when connect calendly integration, then success`() {
        val instructorId = "instructor-123"
        val calendlyAccountId = "calendly-456"

        fixture.given()
            .event(InstructorProfileCreated(
                instructorId = instructorId,
                email = "instructor@dance.com",
                phone = "+1234567890",
                specialties = listOf("Ballet")
            ))
            .`when`()
            .command(ConnectCalendlyIntegration(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId
            ))
            .then()
            .success()
            .events(CalendlyIntegrationConnected(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId
            ))
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(CalendlyIntegrationResult::class.java)
                val integrationResult = result.payload() as CalendlyIntegrationResult
                assertThat(integrationResult.success).isTrue()
                assertThat(integrationResult.integrationId).isEqualTo(calendlyAccountId)
            }
    }

    @Test
    fun `given no instructor profile, when connect calendly integration, then failure`() {
        val instructorId = "instructor-123"
        val calendlyAccountId = "calendly-456"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ConnectCalendlyIntegration(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId
            ))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(CalendlyIntegrationResult::class.java)
                val integrationResult = result.payload() as CalendlyIntegrationResult
                assertThat(integrationResult.success).isFalse()
                assertThat(integrationResult.integrationId).isEmpty()
            }
    }

    @Test
    fun `given calendly integration connected, when update calendly settings, then success`() {
        val instructorId = "instructor-123"
        val calendlyAccountId = "calendly-456"
        val availabilitySettings = "9AM-5PM Mon-Fri"

        fixture.given()
            .event(InstructorProfileCreated(
                instructorId = instructorId,
                email = "instructor@dance.com",
                phone = "+1234567890",
                specialties = listOf("Ballet")
            ))
            .event(CalendlyIntegrationConnected(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId
            ))
            .`when`()
            .command(UpdateCalendlySettings(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId,
                availabilitySettings = availabilitySettings
            ))
            .then()
            .success()
            .events(CalendlySettingsUpdated(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId
            ))
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(CalendlySettingsResult::class.java)
                val settingsResult = result.payload() as CalendlySettingsResult
                assertThat(settingsResult.success).isTrue()
            }
    }

    @Test
    fun `given no calendly integration, when update calendly settings, then failure`() {
        val instructorId = "instructor-123"
        val calendlyAccountId = "calendly-456"
        val availabilitySettings = "9AM-5PM Mon-Fri"

        fixture.given()
            .event(InstructorProfileCreated(
                instructorId = instructorId,
                email = "instructor@dance.com",
                phone = "+1234567890",
                specialties = listOf("Ballet")
            ))
            .`when`()
            .command(UpdateCalendlySettings(
                instructorId = instructorId,
                calendlyAccountId = calendlyAccountId,
                availabilitySettings = availabilitySettings
            ))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(CalendlySettingsResult::class.java)
                val settingsResult = result.payload() as CalendlySettingsResult
                assertThat(settingsResult.success).isFalse()
            }
    }

    @Test
    fun `given instructor profile created and calendly connected, when update settings, then settings updated`() {
        val instructorId = "instructor-789"
        val originalCalendlyAccount = "calendly-original"
        val updatedCalendlyAccount = "calendly-updated"

        fixture.given()
            .event(InstructorProfileCreated(
                instructorId = instructorId,
                email = "instructor@dance.com",
                phone = "+1234567890",
                specialties = listOf("Contemporary", "Modern")
            ))
            .event(CalendlyIntegrationConnected(
                instructorId = instructorId,
                calendlyAccountId = originalCalendlyAccount
            ))
            .`when`()
            .command(UpdateCalendlySettings(
                instructorId = instructorId,
                calendlyAccountId = updatedCalendlyAccount,
                availabilitySettings = "10AM-6PM Weekdays"
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as CalendlySettingsUpdated
                assertThat(event.instructorId).isEqualTo(instructorId)
                assertThat(event.calendlyAccountId).isEqualTo(updatedCalendlyAccount)
            }
    }
}