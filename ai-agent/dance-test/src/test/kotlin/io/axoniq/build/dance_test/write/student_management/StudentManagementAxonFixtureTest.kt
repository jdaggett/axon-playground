package io.axoniq.build.dance_test.write.student_management

import io.axoniq.build.dance_test.student_management.StudentManagementCommandHandler
import io.axoniq.build.dance_test.student_management.StudentManagementState
import io.axoniq.build.dance_test.student_management.api.*
import io.axoniq.build.dance_test.student_management.exception.CannotDeleteStudentWithOutstandingBalance
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Axon Framework test fixture tests for Student Management component.
 * Tests command handling scenarios using event sourcing.
 */
class StudentManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, StudentManagementState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("StudentManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> StudentManagementCommandHandler() }

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
    fun `given no prior activity, when create student profile, then success with events`() {
        val command = CreateStudentProfile(
            instructorId = "instructor123",
            name = "John Doe",
            studentId = "student456",
            phone = "123-456-7890"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val profileCreated = events[0] as StudentProfileCreated
                val relationshipEstablished = events[1] as TrainerStudentRelationshipEstablished

                assertThat(profileCreated.studentId).isEqualTo("student456")
                assertThat(profileCreated.name).isEqualTo("John Doe")
                assertThat(profileCreated.instructorId).isEqualTo("instructor123")
                assertThat(profileCreated.phone).isEqualTo("123-456-7890")

                assertThat(relationshipEstablished.studentId).isEqualTo("student456")
                assertThat(relationshipEstablished.instructorId).isEqualTo("instructor123")
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as StudentProfileResult
                assertThat(payload.success).isTrue()
                assertThat(payload.studentId).isEqualTo("student456")
            }
    }

    @Test
    fun `given student profile created, when delete student profile without balance, then success with events`() {
        val deleteCommand = DeleteStudentProfile(
            instructorId = "instructor123",
            studentId = "student456"
        )

        fixture.given()
            .event(StudentProfileCreated("instructor123", "John Doe", "student456", "123-456-7890"))
            .event(TrainerStudentRelationshipEstablished("instructor123", "student456"))
            .`when`()
            .command(deleteCommand)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val profileDeleted = events[0] as StudentProfileDeleted
                val relationshipTerminated = events[1] as TrainerStudentRelationshipTerminated

                assertThat(profileDeleted.studentId).isEqualTo("student456")
                assertThat(relationshipTerminated.studentId).isEqualTo("student456")
                assertThat(relationshipTerminated.instructorId).isEqualTo("instructor123")
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as StudentDeletionResult
                assertThat(payload.success).isTrue()
            }
    }

    @Test
    fun `given student with no outstanding balance after payment, when delete student profile, then success`() {
        val deleteCommand = DeleteStudentProfile(
            instructorId = "instructor123",
            studentId = "student456"
        )

        fixture.given()
            .event(StudentProfileCreated("instructor123", "John Doe", "student456", "123-456-7890"))
            .event(TrainerStudentRelationshipEstablished("instructor123", "student456"))
            .event(MonetaryBalanceIncreasedFromPayment(100.0, "student456"))
            .`when`()
            .command(deleteCommand)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val profileDeleted = events[0] as StudentProfileDeleted
                val relationshipTerminated = events[1] as TrainerStudentRelationshipTerminated

                assertThat(profileDeleted.studentId).isEqualTo("student456")
                assertThat(relationshipTerminated.studentId).isEqualTo("student456")
                assertThat(relationshipTerminated.instructorId).isEqualTo("instructor123")
            }
    }

    @Test
    fun `given student profile without active relationship, when delete student profile, then only profile deleted`() {
        val deleteCommand = DeleteStudentProfile(
            instructorId = "instructor123",
            studentId = "student456"
        )

        fixture.given()
            .event(StudentProfileCreated("instructor123", "John Doe", "student456", "123-456-7890"))
            .`when`()
            .command(deleteCommand)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val profileDeleted = events[0] as StudentProfileDeleted
                assertThat(profileDeleted.studentId).isEqualTo("student456")
            }
    }

    @Test
    fun `given student with terminated relationship, when delete student profile, then only profile deleted`() {
        val deleteCommand = DeleteStudentProfile(
            instructorId = "instructor123",
            studentId = "student456"
        )

        fixture.given()
            .event(StudentProfileCreated("instructor123", "John Doe", "student456", "123-456-7890"))
            .event(TrainerStudentRelationshipEstablished("instructor123", "student456"))
            .event(TrainerStudentRelationshipTerminated("instructor123", "student456"))
            .`when`()
            .command(deleteCommand)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val profileDeleted = events[0] as StudentProfileDeleted
                assertThat(profileDeleted.studentId).isEqualTo("student456")
            }
    }
}