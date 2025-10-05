package io.axoniq.build.caretrack.write.doctor_registration

import io.axoniq.build.caretrack.doctor_registration.DoctorRegistrationCommandHandler
import io.axoniq.build.caretrack.doctor_registration.DoctorRegistrationState
import io.axoniq.build.caretrack.doctor_registration.api.DoctorRegistered
import io.axoniq.build.caretrack.doctor_registration.api.DoctorRegistrationResult
import io.axoniq.build.caretrack.doctor_registration.api.RegisterDoctor
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
 * Test class for the Doctor Registration Service component using Axon Framework test fixtures.
 * Tests the command handling functionality in a given-when-then format.
 */
class DoctorRegistrationAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, DoctorRegistrationState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("DoctorRegistration")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> DoctorRegistrationCommandHandler() }

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
    fun `given no prior activity, when register doctor, then doctor registered event published`() {
        val command = RegisterDoctor(
            firstName = "John",
            lastName = "Doe",
            email = "john.doe@example.com",
            medicalLicenseNumber = "MD123456",
            specialization = "Cardiology"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as DoctorRegistered
                assertThat(event.firstName).isEqualTo("John")
                assertThat(event.lastName).isEqualTo("Doe")
                assertThat(event.email).isEqualTo("john.doe@example.com")
                assertThat(event.medicalLicenseNumber).isEqualTo("MD123456")
                assertThat(event.specialization).isEqualTo("Cardiology")
                assertThat(event.doctorId).isNotBlank()
            }
            .resultMessageSatisfies { result ->
                val registrationResult = result.payload() as DoctorRegistrationResult
                assertThat(registrationResult.registrationSuccessful).isTrue()
                assertThat(registrationResult.doctorId).isNotBlank()
            }
    }

    @Test
    fun `given doctor already registered, when register doctor again, then registration fails`() {
        val doctorRegisteredEvent = DoctorRegistered(
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            medicalLicenseNumber = "MD789012",
            specialization = "Neurology",
            doctorId = "doctor-123"
        )

        val command = RegisterDoctor(
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            medicalLicenseNumber = "MD789012",
            specialization = "Neurology"
        )

        fixture.given()
            .event(doctorRegisteredEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val registrationResult = result.payload() as DoctorRegistrationResult
                assertThat(registrationResult.registrationSuccessful).isFalse()
                assertThat(registrationResult.doctorId).isEmpty()
            }
    }

    @Test
    fun `given different doctor registered, when register new doctor, then registration succeeds`() {
        val existingDoctorEvent = DoctorRegistered(
            firstName = "Jane",
            lastName = "Smith",
            email = "jane.smith@example.com",
            medicalLicenseNumber = "MD789012",
            specialization = "Neurology",
            doctorId = "doctor-123"
        )

        val command = RegisterDoctor(
            firstName = "Michael",
            lastName = "Johnson",
            email = "michael.johnson@example.com",
            medicalLicenseNumber = "MD345678",
            specialization = "Orthopedics"
        )

        fixture.given()
            .event(existingDoctorEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as DoctorRegistered
                assertThat(event.firstName).isEqualTo("Michael")
                assertThat(event.lastName).isEqualTo("Johnson")
                assertThat(event.email).isEqualTo("michael.johnson@example.com")
                assertThat(event.medicalLicenseNumber).isEqualTo("MD345678")
                assertThat(event.specialization).isEqualTo("Orthopedics")
                assertThat(event.doctorId).isNotBlank()
                assertThat(event.doctorId).isNotEqualTo("doctor-123")
            }
            .resultMessageSatisfies { result ->
                val registrationResult = result.payload() as DoctorRegistrationResult
                assertThat(registrationResult.registrationSuccessful).isTrue()
                assertThat(registrationResult.doctorId).isNotBlank()
            }
    }
}