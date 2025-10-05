package io.axoniq.build.dance_test.write.communication_management

import io.axoniq.build.dance_test.communication_management.CommunicationManagementCommandHandler
import io.axoniq.build.dance_test.communication_management.CommunicationManagementState
import io.axoniq.build.dance_test.communication_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Tests for Communication Management component using Axon Framework test fixture.
 * Verifies command handling, event sourcing, and state management.
 */
class CommunicationManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, CommunicationManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("CommunicationManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> CommunicationManagementCommandHandler() }

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
    fun `given no prior activity, when export financial records, then success`() {
        val instructorId = "instructor-123"
        val command = ExportFinancialRecords(
            startDate = LocalDate.of(2024, 1, 1),
            endDate = LocalDate.of(2024, 12, 31),
            exportFormat = "PDF",
            instructorId = instructorId
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FinancialRecordsExported
                assertThat(event.instructorId).isEqualTo(instructorId)
                assertThat(event.startDate).isEqualTo(LocalDate.of(2024, 1, 1))
                assertThat(event.endDate).isEqualTo(LocalDate.of(2024, 12, 31))
                assertThat(event.exportFormat).isEqualTo("PDF")
            }
            .resultMessageSatisfies { result ->
                val exportResult = result.payload() as FinancialExportResult
                assertThat(exportResult.success).isTrue()
                assertThat(exportResult.exportFileUrl).contains(instructorId)
            }
    }

    @Test
    fun `given no prior activity, when create student waiting list, then success`() {
        val instructorId = "instructor-456"
        val command = CreateStudentWaitingList(instructorId = instructorId)

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as WaitingListCreated
                assertThat(event.instructorId).isEqualTo(instructorId)
            }
            .resultMessageSatisfies { result ->
                val waitingListResult = result.payload() as WaitingListResult
                assertThat(waitingListResult.success).isTrue()
            }
    }

    @Test
    fun `given waiting list already exists, when create student waiting list, then failure`() {
        val instructorId = "instructor-789"
        val command = CreateStudentWaitingList(instructorId = instructorId)

        fixture.given()
            .event(WaitingListCreated(
                creationDate = java.time.LocalDateTime.now(),
                instructorId = instructorId
            ))
            .`when`()
            .command(command)
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val waitingListResult = result.payload() as WaitingListResult
                assertThat(waitingListResult.success).isFalse()
            }
    }

    @Test
    fun `given no prior activity, when send payment reminder, then success`() {
        val instructorId = "instructor-111"
        val studentId = "student-222"
        val command = SendPaymentReminder(
            instructorId = instructorId,
            message = "Please pay your outstanding balance",
            studentId = studentId,
            reminderType = "OVERDUE"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PaymentReminderSent
                assertThat(event.instructorId).isEqualTo(instructorId)
                assertThat(event.studentId).isEqualTo(studentId)
                assertThat(event.reminderType).isEqualTo("OVERDUE")
            }
            .resultMessageSatisfies { result ->
                val reminderResult = result.payload() as PaymentReminderResult
                assertThat(reminderResult.success).isTrue()
            }
    }

    @Test
    fun `given multiple payment reminders sent, when send another reminder, then reminders count increases`() {
        val instructorId = "instructor-333"
        val studentId = "student-444"

        fixture.given()
            .event(PaymentReminderSent(
                instructorId = instructorId,
                sentDate = java.time.LocalDateTime.now().minusDays(1),
                studentId = studentId,
                reminderType = "FIRST"
            ))
            .event(PaymentReminderSent(
                instructorId = instructorId,
                sentDate = java.time.LocalDateTime.now().minusHours(1),
                studentId = studentId,
                reminderType = "SECOND"
            ))
            .`when`()
            .command(SendPaymentReminder(
                instructorId = instructorId,
                message = "Final reminder",
                studentId = studentId,
                reminderType = "FINAL"
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PaymentReminderSent
                assertThat(event.reminderType).isEqualTo("FINAL")
            }
    }
}