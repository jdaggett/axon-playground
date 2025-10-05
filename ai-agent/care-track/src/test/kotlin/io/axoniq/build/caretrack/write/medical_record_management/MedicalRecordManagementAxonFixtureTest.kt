package io.axoniq.build.caretrack.write.medical_record_management

import io.axoniq.build.caretrack.medical_record_management.*
import io.axoniq.build.caretrack.medical_record_management.api.*
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

class MedicalRecordManagementAxonFixtureTest {
    
    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, MedicalRecordManagementState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("MedicalRecordManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> MedicalRecordManagementCommandHandler() }

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
    fun `given no prior activity, when enter patient diagnosis, then diagnosis recorded`() {
        val command = EnterPatientDiagnosis(
            doctorId = "doctor-123",
            patientId = "patient-456",
            severity = "MODERATE",
            notes = "Patient shows symptoms of hypertension",
            condition = "Hypertension",
            diagnosisDate = LocalDate.of(2024, 1, 15)
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DiagnosisEntryResult
                assertThat(payload.diagnosisRecorded).isTrue()
                assertThat(payload.diagnosisId).isNotEmpty()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PatientDiagnosisRecorded
                assertThat(event.doctorId).isEqualTo("doctor-123")
                assertThat(event.patientId).isEqualTo("patient-456")
                assertThat(event.condition).isEqualTo("Hypertension")
                assertThat(event.severity).isEqualTo("MODERATE")
                assertThat(event.diagnosisDate).isEqualTo(LocalDate.of(2024, 1, 15))
            }
    }
    
    @Test
    fun `given diagnosis recorded, when remove patient diagnosis, then diagnosis removed`() {
        val diagnosisId = "diagnosis-789"
        val recordedEvent = PatientDiagnosisRecorded(
            doctorId = "doctor-123",
            patientId = "patient-456",
            severity = "MODERATE",
            notes = "Patient shows symptoms of hypertension",
            diagnosisId = diagnosisId,
            condition = "Hypertension",
            diagnosisDate = LocalDate.of(2024, 1, 15)
        )

        val command = RemovePatientDiagnosis(
            doctorId = "doctor-123",
            patientId = "patient-456",
            diagnosisId = diagnosisId
        )

        fixture.given()
            .event(recordedEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DiagnosisRemovalResult
                assertThat(payload.diagnosisRemoved).isTrue()
            }
            .events(PatientDiagnosisRemoved(
                doctorId = "doctor-123",
                patientId = "patient-456",
                diagnosisId = diagnosisId
            ))
    }

    @Test
    fun `given no diagnosis exists, when remove patient diagnosis, then removal fails`() {
        val command = RemovePatientDiagnosis(
            doctorId = "doctor-123",
            patientId = "patient-456",
            diagnosisId = "non-existent-diagnosis"
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as DiagnosisRemovalResult
                assertThat(payload.diagnosisRemoved).isFalse()
            }
            .noEvents()
    }
    
    @Test
    fun `given no prior activity, when prescribe treatment, then treatment prescribed`() {
        val command = PrescribeTreatment(
            doctorId = "doctor-123",
            frequency = "Twice daily",
            dosage = "10mg",
            patientId = "patient-456",
            medicationName = "Lisinopril",
            duration = "30 days"
        )
        
        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TreatmentPrescriptionResult
                assertThat(payload.treatmentPrescribed).isTrue()
                assertThat(payload.treatmentId).isNotEmpty()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as TreatmentPrescribed
                assertThat(event.doctorId).isEqualTo("doctor-123")
                assertThat(event.patientId).isEqualTo("patient-456")
                assertThat(event.medicationName).isEqualTo("Lisinopril")
                assertThat(event.dosage).isEqualTo("10mg")
                assertThat(event.frequency).isEqualTo("Twice daily")
                assertThat(event.duration).isEqualTo("30 days")
            }
    }

    @Test
    fun `given treatment prescribed, when discontinue treatment, then treatment discontinued`() {
        val treatmentId = "treatment-abc"
        val prescribedEvent = TreatmentPrescribed(
            doctorId = "doctor-123",
            frequency = "Twice daily",
            dosage = "10mg",
            patientId = "patient-456",
            medicationName = "Lisinopril",
            duration = "30 days",
            treatmentId = treatmentId
        )

        val command = DiscontinueTreatment(
            doctorId = "doctor-123",
            reason = "Patient experienced side effects",
            patientId = "patient-456",
            treatmentId = treatmentId
        )

        fixture.given()
            .event(prescribedEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TreatmentDiscontinuationResult
                assertThat(payload.treatmentDiscontinued).isTrue()
            }
            .events(TreatmentDiscontinued(
                doctorId = "doctor-123",
                reason = "Patient experienced side effects",
                patientId = "patient-456",
                treatmentId = treatmentId
            ))
    }

    @Test
    fun `given no treatment exists, when discontinue treatment, then discontinuation fails`() {
        val command = DiscontinueTreatment(
            doctorId = "doctor-123",
            reason = "No longer needed",
            patientId = "patient-456",
            treatmentId = "non-existent-treatment"
        )
        
        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TreatmentDiscontinuationResult
                assertThat(payload.treatmentDiscontinued).isFalse()
            }
            .noEvents()
    }
    
    @Test
    fun `given treatment already discontinued, when discontinue treatment again, then discontinuation fails`() {
        val treatmentId = "treatment-abc"
        val prescribedEvent = TreatmentPrescribed(
            doctorId = "doctor-123",
            frequency = "Twice daily",
            dosage = "10mg",
            patientId = "patient-456",
            medicationName = "Lisinopril",
            duration = "30 days",
            treatmentId = treatmentId
        )
        val discontinuedEvent = TreatmentDiscontinued(
            doctorId = "doctor-123",
            reason = "Side effects",
            patientId = "patient-456",
            treatmentId = treatmentId
        )

        val command = DiscontinueTreatment(
            doctorId = "doctor-123",
            reason = "No longer needed",
            patientId = "patient-456",
            treatmentId = treatmentId
        )

        fixture.given()
            .event(prescribedEvent)
            .event(discontinuedEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TreatmentDiscontinuationResult
                assertThat(payload.treatmentDiscontinued).isFalse()
            }
            .noEvents()
    }
}