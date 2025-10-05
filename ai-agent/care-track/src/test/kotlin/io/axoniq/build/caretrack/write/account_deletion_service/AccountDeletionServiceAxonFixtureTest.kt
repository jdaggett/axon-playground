package io.axoniq.build.caretrack.write.account_deletion_service

import io.axoniq.build.caretrack.account_deletion_service.*
import io.axoniq.build.caretrack.account_deletion_service.api.*
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
 * Axon test fixture tests for Account Deletion Service
 * Tests command handling and event sourcing behavior
 */
class AccountDeletionServiceAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, AccountDeletionState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("AccountDeletionService")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> AccountDeletionServiceCommandHandler() }
         
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
    fun `given no prior activity, when delete patient account, then patient account deleted event emitted`() {
        val patientId = "patient-123"
        val confirmationCode = "CONFIRM-123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(DeletePatientAccount(confirmationCode, patientId))
            .then()
            .success()
            .events(PatientAccountDeleted(patientId))
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(PatientAccountDeletionResult::class.java)
                val deletionResult = result.payload() as PatientAccountDeletionResult
                assertThat(deletionResult.accountDeleted).isTrue()
            }
    }

    @Test
    fun `given no prior activity, when delete doctor account, then doctor account deleted event emitted`() {
        val doctorId = "doctor-456"
        val confirmationCode = "CONFIRM-456"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(DeleteDoctorAccount(confirmationCode, doctorId))
            .then()
            .success()
            .events(DoctorAccountDeleted(doctorId))
            .resultMessageSatisfies { result ->
                assertThat(result.payload()).isInstanceOf(DoctorAccountDeletionResult::class.java)
                val deletionResult = result.payload() as DoctorAccountDeletionResult
                assertThat(deletionResult.accountDeleted).isTrue()
            }
    }

    @Test
    fun `given blank confirmation code, when delete patient account, then exception thrown`() {
        val patientId = "patient-123"
        val blankConfirmationCode = ""

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(DeletePatientAccount(blankConfirmationCode, patientId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Confirmation code cannot be blank")
            }
    }

    @Test
    fun `given blank confirmation code, when delete doctor account, then exception thrown`() {
        val doctorId = "doctor-456"
        val blankConfirmationCode = ""

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(DeleteDoctorAccount(blankConfirmationCode, doctorId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Confirmation code cannot be blank")
            }
    }

    @Test
    fun `given patient account deleted, when processing patient deletion event, then state updated correctly`() {
        val patientId = "patient-123"

        fixture.given()
            .event(PatientAccountDeleted(patientId))
            .`when`()
            .nothingHappens()
            .then()
            .success()
    }

    @Test
    fun `given doctor account deleted, when processing doctor deletion event, then state updated correctly`() {
        val doctorId = "doctor-456"

        fixture.given()
            .event(DoctorAccountDeleted(doctorId))
            .`when`()
            .nothingHappens()
            .then()
            .success()
    }
}