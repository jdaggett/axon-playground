package io.axoniq.build.caretrack.write.appointment_management

import io.axoniq.build.caretrack.appointment_management.*
import io.axoniq.build.caretrack.appointment_management.api.*
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
 * Test class for the Appointment Management Service component using Axon Test Fixture.
 * Tests command handling and event sourcing functionality.
 */
class AppointmentManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, AppointmentManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("AppointmentManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> AppointmentManagementServiceCommandHandler() }

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
    fun `given no prior activity, when schedule patient appointment, then appointment scheduled event published`() {
        val appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0)
        val command = SchedulePatientAppointment(
            doctorId = "doctor123",
            purpose = "Regular checkup",
            patientId = "patient456",
            appointmentDate = appointmentDate
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as AppointmentScheduled
                assertThat(event.patientId).isEqualTo("patient456")
                assertThat(event.doctorId).isEqualTo("doctor123")
                assertThat(event.purpose).isEqualTo("Regular checkup")
                assertThat(event.appointmentDate).isEqualTo(appointmentDate)
                assertThat(event.appointmentId).isNotBlank()
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AppointmentSchedulingResult
                assertThat(payload.appointmentScheduled).isTrue()
                assertThat(payload.appointmentId).isNotBlank()
            }
    }

    @Test
    fun `given appointment scheduled, when mark appointment missed, then appointment missed event published`() {
        val appointmentId = "appointment789"
        val appointmentScheduled = AppointmentScheduled(
            patientId = "patient456",
            doctorId = "doctor123",
            purpose = "Regular checkup",
            appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0),
            appointmentId = appointmentId
        )

        val command = MarkAppointmentMissed(
            doctorId = "doctor123",
            appointmentId = appointmentId
        )

        fixture.given()
            .event(appointmentScheduled)
            .`when`()
            .command(command)
            .then()
            .success()
            .events(AppointmentMissed(doctorId = "doctor123", appointmentId = appointmentId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AppointmentMissedResult
                assertThat(payload.missedStatusMarked).isTrue()
            }
    }

    @Test
    fun `given appointment scheduled, when mark appointment attended, then appointment attended event published`() {
        val appointmentId = "appointment789"
        val appointmentScheduled = AppointmentScheduled(
            patientId = "patient456",
            doctorId = "doctor123",
            purpose = "Regular checkup",
            appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0),
            appointmentId = appointmentId
        )

        val command = MarkAppointmentAttended(
            doctorId = "doctor123",
            appointmentId = appointmentId
        )

        fixture.given()
            .event(appointmentScheduled)
            .`when`()
            .command(command)
            .then()
            .success()
            .events(AppointmentAttended(doctorId = "doctor123", appointmentId = appointmentId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AppointmentAttendanceResult
                assertThat(payload.attendanceMarked).isTrue()
            }
    }

    @Test
    fun `given appointment scheduled, when cancel patient appointment, then appointment cancelled event published`() {
        val appointmentId = "appointment789"
        val appointmentScheduled = AppointmentScheduled(
            patientId = "patient456",
            doctorId = "doctor123",
            purpose = "Regular checkup",
            appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0),
            appointmentId = appointmentId
        )

        val command = CancelPatientAppointment(
            doctorId = "doctor123",
            cancellationReason = "Patient unavailable",
            appointmentId = appointmentId
        )

        fixture.given()
            .event(appointmentScheduled)
            .`when`()
            .command(command)
            .then()
            .success()
            .events(AppointmentCancelled(
                doctorId = "doctor123",
                cancellationReason = "Patient unavailable",
                appointmentId = appointmentId
            ))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AppointmentCancellationResult
                assertThat(payload.appointmentCancelled).isTrue()
            }
    }

    @Test
    fun `given no appointment, when mark appointment missed, then exception thrown`() {
        val command = MarkAppointmentMissed(
            doctorId = "doctor123",
            appointmentId = "nonexistent"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Appointment with id nonexistent not found")
            }
    }

    @Test
    fun `given appointment already missed, when mark appointment missed again, then exception thrown`() {
        val appointmentId = "appointment789"
        val appointmentScheduled = AppointmentScheduled(
            patientId = "patient456",
            doctorId = "doctor123",
            purpose = "Regular checkup",
            appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0),
            appointmentId = appointmentId
        )
        val appointmentMissed = AppointmentMissed(
            doctorId = "doctor123",
            appointmentId = appointmentId
        )

        val command = MarkAppointmentMissed(
            doctorId = "doctor123",
            appointmentId = appointmentId
        )

        fixture.given()
            .event(appointmentScheduled)
            .event(appointmentMissed)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Appointment is already marked as missed")
            }
    }

    @Test
    fun `given cancelled appointment, when mark appointment attended, then exception thrown`() {
        val appointmentId = "appointment789"
        val appointmentScheduled = AppointmentScheduled(
            patientId = "patient456",
            doctorId = "doctor123",
            purpose = "Regular checkup",
            appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0),
            appointmentId = appointmentId
        )
        val appointmentCancelled = AppointmentCancelled(
            doctorId = "doctor123",
            cancellationReason = "Doctor unavailable",
            appointmentId = appointmentId
        )

        val command = MarkAppointmentAttended(
            doctorId = "doctor123",
            appointmentId = appointmentId
        )

        fixture.given()
            .event(appointmentScheduled)
            .event(appointmentCancelled)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Cannot mark cancelled appointment as attended")
            }
    }

    @Test
    fun `given appointment already cancelled, when cancel appointment again, then exception thrown`() {
        val appointmentId = "appointment789"
        val appointmentScheduled = AppointmentScheduled(
            patientId = "patient456",
            doctorId = "doctor123",
            purpose = "Regular checkup",
            appointmentDate = LocalDateTime.of(2024, 1, 15, 10, 0),
            appointmentId = appointmentId
        )
        val appointmentCancelled = AppointmentCancelled(
            doctorId = "doctor123",
            cancellationReason = "Doctor unavailable",
            appointmentId = appointmentId
        )

        val command = CancelPatientAppointment(
            doctorId = "doctor123",
            cancellationReason = "Patient changed mind",
            appointmentId = appointmentId
        )

        fixture.given()
            .event(appointmentScheduled)
            .event(appointmentCancelled)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Appointment is already cancelled")
            }
    }
}