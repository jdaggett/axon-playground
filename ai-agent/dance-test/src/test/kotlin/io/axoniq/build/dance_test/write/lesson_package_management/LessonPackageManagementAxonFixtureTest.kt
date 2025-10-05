package io.axoniq.build.dance_test.write.lesson_package_management

import io.axoniq.build.dance_test.lesson_package_management.LessonPackageManagementCommandHandler
import io.axoniq.build.dance_test.lesson_package_management.LessonPackageManagementState
import io.axoniq.build.dance_test.lesson_package_management.api.*
import io.axoniq.build.dance_test.lesson_package_management.exception.CannotDeleteActiveLessonPackage
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
 * Axon test fixture tests for the Lesson Package Management component.
 * Tests command handling, event sourcing, and exception scenarios.
 */
class LessonPackageManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, LessonPackageManagementState::class.java)
        
        val commandHandlingModule = CommandHandlingModule
            .named("LessonPackageManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> LessonPackageManagementCommandHandler() }

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
    fun `given no prior activity, when create custom lesson package, then package created with balance and transaction events`() {
        val packageId = "package-001"
        val studentId = "student-001"
        val instructorId = "instructor-001"
        val lessonCount = 10
        val lessonDuration = 60
        val price = 500.0

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateCustomLessonPackage(
                packageId = packageId,
                studentId = studentId,
                instructorId = instructorId,
                lessonCount = lessonCount,
                lessonDuration = lessonDuration,
                price = price
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(3)

                val packageCreatedEvent = events[0] as CustomLessonPackageCreated
                assertThat(packageCreatedEvent.packageId).isEqualTo(packageId)
                assertThat(packageCreatedEvent.studentId).isEqualTo(studentId)
                assertThat(packageCreatedEvent.instructorId).isEqualTo(instructorId)
                assertThat(packageCreatedEvent.lessonCount).isEqualTo(lessonCount)
                assertThat(packageCreatedEvent.lessonDuration).isEqualTo(lessonDuration)
                assertThat(packageCreatedEvent.price).isEqualTo(price)

                val balanceEvent = events[1] as LessonBalanceIncreasedFromPackage
                assertThat(balanceEvent.packageId).isEqualTo(packageId)
                assertThat(balanceEvent.studentId).isEqualTo(studentId)
                assertThat(balanceEvent.lessonCount).isEqualTo(lessonCount)

                val transactionEvent = events[2] as TransactionRecordCreated
                assertThat(transactionEvent.studentId).isEqualTo(studentId)
                assertThat(transactionEvent.transactionType).isEqualTo("LESSON_PACKAGE_PURCHASE")
                assertThat(transactionEvent.amount).isEqualTo(price)
                assertThat(transactionEvent.description).contains(packageId)
            }
            .resultMessageSatisfies { result ->
                val lessonPackageResult = result.payload() as LessonPackageResult
                assertThat(lessonPackageResult.success).isTrue()
                assertThat(lessonPackageResult.packageId).isEqualTo(packageId)
            }
    }
    
    @Test
    fun `given non-existing package, when delete lesson package, then exception thrown`() {
        val packageId = "non-existing-package"
        val studentId = "student-001"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(DeleteLessonPackage(
                packageId = packageId,
                studentId = studentId
            ))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("does not exist")
            }
    }

    @Test
    fun `given active lesson package, when delete lesson package, then cannot delete exception thrown`() {
        val packageId = "package-001"
        val studentId = "student-001"
        val instructorId = "instructor-001"

        fixture.given()
            .event(CustomLessonPackageCreated(
                packageId = packageId,
                studentId = studentId,
                instructorId = instructorId,
                lessonCount = 10,
                lessonDuration = 60,
                price = 500.0
            ))
            .`when`()
            .command(DeleteLessonPackage(
                packageId = packageId,
                studentId = studentId
            ))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(CannotDeleteActiveLessonPackage::class.java)
                    .hasMessageContaining("Cannot delete active lesson package")
            }
    }
    
    @Test
    fun `given inactive lesson package, when delete lesson package, then package deleted successfully`() {
        val packageId = "package-001"
        val studentId = "student-001"
        val instructorId = "instructor-001"

        fixture.given()
            .event(CustomLessonPackageCreated(
                packageId = packageId,
                studentId = studentId,
                instructorId = instructorId,
                lessonCount = 10,
                lessonDuration = 60,
                price = 500.0
            ))
            .event(LessonPackageDeleted(packageId = packageId))
            .`when`()
            .command(DeleteLessonPackage(
                packageId = packageId,
                studentId = studentId
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as LessonPackageDeleted
                assertThat(event.packageId).isEqualTo(packageId)
            }
            .resultMessageSatisfies { result ->
                val deletionResult = result.payload() as LessonPackageDeletionResult
                assertThat(deletionResult.success).isTrue()
            }
    }

    @Test
    fun `given custom lesson package created, when create another package with same id, then new package created`() {
        val packageId = "package-001"
        val studentId = "student-001"
        val instructorId = "instructor-002"

        fixture.given()
            .event(CustomLessonPackageCreated(
                packageId = packageId,
                studentId = "other-student",
                instructorId = "other-instructor",
                lessonCount = 5,
                lessonDuration = 45,
                price = 300.0
            ))
            .`when`()
            .command(CreateCustomLessonPackage(
                packageId = packageId,
                studentId = studentId,
                instructorId = instructorId,
                lessonCount = 15,
                lessonDuration = 90,
                price = 750.0
            ))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(3)
                assertThat(events[0]).isInstanceOf(CustomLessonPackageCreated::class.java)
                assertThat(events[1]).isInstanceOf(LessonBalanceIncreasedFromPackage::class.java)
                assertThat(events[2]).isInstanceOf(TransactionRecordCreated::class.java)
            }
    }
}