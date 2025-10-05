package io.axoniq.build.dance_test.write.booking_access_management

import io.axoniq.build.dance_test.booking_access_management.*
import io.axoniq.build.dance_test.booking_access_management.api.*
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
 * BookingAccessManagementAxonFixtureTest - Tests for booking access management component
 * Verifies command handling, event generation and state evolution
 */
class BookingAccessManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, BookingAccessManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("BookingAccessManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> BookingAccessManagementCommandHandler() }

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
    fun `given no prior activity, when block student booking access, then success`() {
        val studentId = "student123"
        val instructorId = "instructor456"
        val blockingReason = "Inappropriate behavior"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(BlockStudentBookingAccess(instructorId, blockingReason, studentId))
            .then()
            .success()
            .events(BookingAccessBlocked(instructorId, blockingReason, studentId))
    }

    @Test
    fun `given no prior activity, when update booking access, then success`() {
        val studentId = "student123"
        val instructorId = "instructor456"
        val newAccessStatus = "ACTIVE"
        val reason = "Good behavior restored"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(UpdateBookingAccess(instructorId, newAccessStatus, reason, studentId))
            .then()
            .success()
            .events(BookingAccessStatusUpdated(instructorId, newAccessStatus, reason, studentId))
    }

    @Test
    fun `given no prior activity, when handle blocking with balances, then success`() {
        val studentId = "student123"
        val instructorId = "instructor456"
        val preserveBalances = true

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(HandleBlockingWithBalances(instructorId, preserveBalances, studentId))
            .then()
            .success()
            .events(BlockingBalanceDecisionRecorded(instructorId, preserveBalances, studentId))
    }

    @Test
    fun `given booking access blocked, when update booking access, then status updated`() {
        val studentId = "student123"
        val instructorId = "instructor456"
        val blockingReason = "Payment issues"
        val newAccessStatus = "ACTIVE"
        val updateReason = "Payment resolved"

        fixture.given()
            .event(BookingAccessBlocked(instructorId, blockingReason, studentId))
            .`when`()
            .command(UpdateBookingAccess(instructorId, newAccessStatus, updateReason, studentId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BookingAccessStatusUpdated
                assertThat(event.studentId).isEqualTo(studentId)
                assertThat(event.newAccessStatus).isEqualTo(newAccessStatus)
                assertThat(event.reason).isEqualTo(updateReason)
            }
    }

    @Test
    fun `given balance decision recorded, when block student access, then access blocked`() {
        val studentId = "student123"
        val instructorId = "instructor456"
        val preserveBalances = false
        val blockingReason = "Violating terms"

        fixture.given()
            .event(BlockingBalanceDecisionRecorded(instructorId, preserveBalances, studentId))
            .`when`()
            .command(BlockStudentBookingAccess(instructorId, blockingReason, studentId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BookingAccessBlocked
                assertThat(event.studentId).isEqualTo(studentId)
                assertThat(event.blockingReason).isEqualTo(blockingReason)
                assertThat(event.instructorId).isEqualTo(instructorId)
            }
    }

    @Test
    fun `given booking access status updated, when handle balances, then balance decision recorded`() {
        val studentId = "student123"
        val instructorId = "instructor456"
        val accessStatus = "SUSPENDED"
        val preserveBalances = true

        fixture.given()
            .event(BookingAccessStatusUpdated(instructorId, accessStatus, null, studentId))
            .`when`()
            .command(HandleBlockingWithBalances(instructorId, preserveBalances, studentId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BlockingBalanceDecisionRecorded
                assertThat(event.studentId).isEqualTo(studentId)
                assertThat(event.preserveBalances).isEqualTo(preserveBalances)
                assertThat(event.instructorId).isEqualTo(instructorId)
            }
    }
}