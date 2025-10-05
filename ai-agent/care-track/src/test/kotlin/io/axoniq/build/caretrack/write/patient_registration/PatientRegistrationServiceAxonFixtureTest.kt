package io.axoniq.build.caretrack.write.patient_registration

import io.axoniq.build.caretrack.patient_registration.PatientRegistrationServiceCommandHandler
import io.axoniq.build.caretrack.patient_registration.PatientRegistrationServiceState
import io.axoniq.build.caretrack.patient_registration.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Test class for the Patient Registration Service component using Axon Framework's test fixture.
 * Verifies command handling, event publishing, and exception scenarios for patient registration.
 */
class PatientRegistrationServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, PatientRegistrationServiceState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("PatientRegistrationService")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> PatientRegistrationServiceCommandHandler() }

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
    fun `given no prior activity, when register patient, then patient registered successfully`() {
        val command = RegisterPatient(
            firstName = "John",
            email = "john.doe@example.com",
            dateOfBirth = LocalDate.of(1990, 5, 15),
            lastName = "Doe"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PatientRegistrationResult
                assertThat(payload.registrationSuccessful).isTrue()
                assertThat(payload.patientId).isNotEmpty()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PatientRegistered
                assertThat(event.firstName).isEqualTo("John")
                assertThat(event.lastName).isEqualTo("Doe")
                assertThat(event.email).isEqualTo("john.doe@example.com")
                assertThat(event.dateOfBirth).isEqualTo(LocalDate.of(1990, 5, 15))
                assertThat(event.patientId).isNotEmpty()
            }
    }

    @Test
    fun `given patient already registered, when register same patient, then registration fails`() {
        val patientId = "existing-patient-id"
        val existingEvent = PatientRegistered(
            firstName = "John",
            email = "john.doe@example.com",
            patientId = patientId,
            dateOfBirth = LocalDate.of(1990, 5, 15),
            lastName = "Doe"
        )

        val command = RegisterPatient(
            firstName = "John",
            email = "john.doe@example.com",
            dateOfBirth = LocalDate.of(1990, 5, 15),
            lastName = "Doe"
        )

        fixture.given()
            .event(existingEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PatientRegistrationResult
                assertThat(payload.registrationSuccessful).isFalse()
                assertThat(payload.patientId).isEmpty()
            }
            .noEvents()
    }

    @Test
    fun `given new patient with different details, when register patient, then registration succeeds`() {
        val command = RegisterPatient(
            firstName = "Jane",
            email = "jane.smith@example.com",
            dateOfBirth = LocalDate.of(1985, 12, 10),
            lastName = "Smith"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PatientRegistrationResult
                assertThat(payload.registrationSuccessful).isTrue()
                assertThat(payload.patientId).isNotEmpty()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PatientRegistered
                assertThat(event.firstName).isEqualTo("Jane")
                assertThat(event.lastName).isEqualTo("Smith")
                assertThat(event.email).isEqualTo("jane.smith@example.com")
                assertThat(event.dateOfBirth).isEqualTo(LocalDate.of(1985, 12, 10))
                assertThat(event.patientId).isNotEmpty()
            }
    }
}