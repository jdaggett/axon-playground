package io.axoniq.build.dance_test.write.session_management

import io.axoniq.build.dance_test.session_management.*
import io.axoniq.build.dance_test.session_management.api.*
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
 * Test class for the Session Management component using Axon Framework test fixture.
 * Tests command handlers and event sourcing functionality.
 */
class SessionManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, SessionManagementState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("SessionManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> SessionManagementCommandHandler() }
        
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
    fun `given scheduled session, when complete training session, then session completed successfully`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        
        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .`when`()
            .command(CompleteTrainingSession(sessionId, 55, "Great progress today"))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(3)

                val sessionCompleted = events[0] as SessionCompleted
                assertThat(sessionCompleted.sessionId).isEqualTo(sessionId)
                assertThat(sessionCompleted.actualDuration).isEqualTo(55)

                val balanceDecreased = events[1] as LessonBalanceDecreasedFromSession
                assertThat(balanceDecreased.studentId).isEqualTo(studentId)
                assertThat(balanceDecreased.sessionId).isEqualTo(sessionId)
                assertThat(balanceDecreased.lessonsUsed).isEqualTo(1)
                
                val notesRecorded = events[2] as SessionNotesRecorded
                assertThat(notesRecorded.sessionId).isEqualTo(sessionId)
                assertThat(notesRecorded.notes).isEqualTo("Great progress today")
            }
    }

    @Test
    fun `given completed session, when complete training session, then exception thrown`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        
        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .event(SessionCompleted(sessionId, 60, LocalDateTime.now()))
            .`when`()
            .command(CompleteTrainingSession(sessionId, 55, null))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("has already been completed")
            }
    }

    @Test
    fun `given scheduled session, when modify session details, then session details modified`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        val newDate = LocalDateTime.now().plusDays(2)

        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .`when`()
            .command(ModifySessionDetails(sessionId, 90, newDate))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as SessionDetailsModified
                assertThat(event.sessionId).isEqualTo(sessionId)
                assertThat(event.newDuration).isEqualTo(90)
                assertThat(event.newSessionDate).isEqualTo(newDate)
            }
    }

    @Test
    fun `given completed session, when modify session details, then exception thrown`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)

        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .event(SessionCompleted(sessionId, 60, LocalDateTime.now()))
            .`when`()
            .command(ModifySessionDetails(sessionId, 90, null))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Cannot modify a completed session")
            }
    }

    @Test
    fun `given scheduled session, when mark as no show, then session marked as no show`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)

        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .`when`()
            .command(MarkSessionAsNoShow(sessionId, "Student did not show up", true))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as SessionMarkedAsNoShow
                assertThat(event.sessionId).isEqualTo(sessionId)
                assertThat(event.reason).isEqualTo("Student did not show up")
                assertThat(event.chargeStudent).isTrue()
            }
    }

    @Test
    fun `given no show session, when mark as no show, then exception thrown`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)

        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .event(SessionMarkedAsNoShow(sessionId, "Previous no-show", false))
            .`when`()
            .command(MarkSessionAsNoShow(sessionId, "Student did not show up", true))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("is already marked as no-show")
            }
    }

    @Test
    fun `given scheduled session, when complete with reduced time, then session completed with full charge`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)
        
        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .`when`()
            .command(CompleteSessionWithReducedTime(sessionId, 30, "Session ended early"))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)

                val sessionCompleted = events[0] as SessionCompletedWithFullCharge
                assertThat(sessionCompleted.sessionId).isEqualTo(sessionId)
                assertThat(sessionCompleted.actualDuration).isEqualTo(30)
                assertThat(sessionCompleted.fullChargeApplied).isTrue()
                
                val notesRecorded = events[1] as SessionNotesRecorded
                assertThat(notesRecorded.sessionId).isEqualTo(sessionId)
                assertThat(notesRecorded.notes).isEqualTo("Session ended early")
            }
    }

    @Test
    fun `given no show session, when complete with reduced time, then exception thrown`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)

        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .event(SessionMarkedAsNoShow(sessionId, "Student no-show", true))
            .`when`()
            .command(CompleteSessionWithReducedTime(sessionId, 30, null))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Cannot complete a session marked as no-show")
            }
    }

    @Test
    fun `given scheduled session, when complete training session without notes, then only session completed and balance decreased`() {
        val sessionId = "session-123"
        val studentId = "student-456"
        val instructorId = "instructor-789"
        val sessionDate = LocalDateTime.now().plusDays(1)

        fixture.given()
            .event(SessionScheduled(sessionId, studentId, instructorId, 60, sessionDate))
            .`when`()
            .command(CompleteTrainingSession(sessionId, 60, null))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)

                val sessionCompleted = events[0] as SessionCompleted
                assertThat(sessionCompleted.sessionId).isEqualTo(sessionId)
                assertThat(sessionCompleted.actualDuration).isEqualTo(60)

                val balanceDecreased = events[1] as LessonBalanceDecreasedFromSession
                assertThat(balanceDecreased.studentId).isEqualTo(studentId)
                assertThat(balanceDecreased.sessionId).isEqualTo(sessionId)
                assertThat(balanceDecreased.lessonsUsed).isEqualTo(1)
            }
    }
}