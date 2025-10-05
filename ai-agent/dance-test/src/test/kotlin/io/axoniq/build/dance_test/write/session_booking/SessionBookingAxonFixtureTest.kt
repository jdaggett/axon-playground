package io.axoniq.build.dance_test.write.session_booking

import io.axoniq.build.dance_test.session_booking.*
import io.axoniq.build.dance_test.session_booking.api.*
import io.axoniq.build.dance_test.session_booking.exception.CreditLimitExceeded
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

class SessionBookingAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, SessionBookingState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("SessionBooking")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> SessionBookingCommandHandler() }
         
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
    fun `given no prior activity, when create session booking, then session scheduled`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        
        val command = CreateSessionBooking(
            instructorId = instructorId,
            duration = 60,
            sessionDate = sessionDate,
            studentId = studentId,
            sessionId = sessionId
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .events(SessionScheduled(instructorId, 60, sessionDate, studentId, sessionId))
    }

    @Test
    fun `given session scheduled, when cancel session booking, then session cancelled`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        val cancellationTime = LocalDateTime.now()

        val cancelCommand = CancelSessionBooking(
            cancellationTime = cancellationTime,
            sessionId = sessionId
        )

        fixture.given()
            .event(SessionScheduled(instructorId, 60, sessionDate, studentId, sessionId))
            .`when`()
            .command(cancelCommand)
            .then()
            .success()
            .events(SessionCancelled(cancellationTime, sessionId))
    }

    @Test
    fun `given session scheduled, when cancel booking late, then session cancelled and lesson forfeited`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusHours(12) // Less than 24 hours
        val cancellationTime = LocalDateTime.now()

        val cancelCommand = CancelSessionBooking(
            cancellationTime = cancellationTime,
            sessionId = sessionId
        )

        fixture.given()
            .event(SessionScheduled(instructorId, 60, sessionDate, studentId, sessionId))
            .`when`()
            .command(cancelCommand)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                assertThat(events[0]).isInstanceOf(SessionCancelled::class.java)
                assertThat(events[1]).isInstanceOf(LessonForfeitedForLateCancellation::class.java)
                val forfeitEvent = events[1] as LessonForfeitedForLateCancellation
                assertThat(forfeitEvent.lessonsForfeited).isEqualTo(1)
                assertThat(forfeitEvent.studentId).isEqualTo(studentId)
                assertThat(forfeitEvent.sessionId).isEqualTo(sessionId)
            }
    }

    @Test
    fun `given no prior activity, when create negative balance session within limit, then session scheduled with negative balance`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        val command = CreateNegativeBalanceSession(
            instructorId = instructorId,
            duration = 60,
            sessionDate = sessionDate,
            studentId = studentId,
            sessionId = sessionId
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as SessionScheduledWithNegativeBalance
                assertThat(event.studentId).isEqualTo(studentId)
                assertThat(event.sessionId).isEqualTo(sessionId)
                assertThat(event.negativeBalance).isEqualTo(0.0)
            }
    }

    @Test
    fun `given student beyond credit limit, when create negative balance session, then exception thrown`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)

        val command = CreateNegativeBalanceSession(
            instructorId = instructorId,
            duration = 60,
            sessionDate = sessionDate,
            studentId = studentId,
            sessionId = sessionId
        )

        fixture.given()
            .event(SessionScheduledWithNegativeBalance(studentId, -150.0, sessionId))
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(CreditLimitExceeded::class.java)
                    .hasMessageContaining("Student has exceeded credit limit")
            }
    }

    @Test
    fun `given session cancelled, when decide cancellation charges, then decision recorded`() {
        val sessionId = "session-123"
        val cancellationTime = LocalDateTime.now()

        val command = DecideCancellationCharges(
            chargeStudent = true,
            reason = "Late cancellation",
            sessionId = sessionId
        )

        fixture.given()
            .event(SessionCancelled(cancellationTime, sessionId))
            .`when`()
            .command(command)
            .then()
            .success()
            .events(CancellationDecisionRecorded(true, "Late cancellation", sessionId))
    }

    @Test
    fun `given session with negative balance, when acknowledge debt, then debt acknowledgment recorded`() {
        val sessionId = "session-123"
        val studentId = "student-456"

        val command = AcknowledgeDebtAccumulation(
            studentId = studentId,
            sessionId = sessionId
        )

        fixture.given()
            .event(SessionScheduledWithNegativeBalance(studentId, -50.0, sessionId))
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as DebtAcknowledgmentRecorded
                assertThat(event.studentId).isEqualTo(studentId)
                assertThat(event.sessionId).isEqualTo(sessionId)
                assertThat(event.acknowledgedAmount).isEqualTo(50.0)
            }
    }

    @Test
    fun `given student beyond reasonable limit, when acknowledge debt, then beyond limit event recorded`() {
        val sessionId = "session-123"
        val studentId = "student-456"

        val command = AcknowledgeDebtAccumulation(
            studentId = studentId,
            sessionId = sessionId
        )

        fixture.given()
            .event(SessionScheduledWithNegativeBalance(studentId, -150.0, sessionId))
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                assertThat(events[0]).isInstanceOf(DebtAcknowledgmentRecorded::class.java)
                assertThat(events[1]).isInstanceOf(SessionScheduledBeyondLimit::class.java)
                val beyondLimitEvent = events[1] as SessionScheduledBeyondLimit
                assertThat(beyondLimitEvent.studentId).isEqualTo(studentId)
                assertThat(beyondLimitEvent.sessionId).isEqualTo(sessionId)
            }
    }
}