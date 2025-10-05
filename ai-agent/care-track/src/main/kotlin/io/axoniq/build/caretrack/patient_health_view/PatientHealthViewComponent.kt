package io.axoniq.build.caretrack.patient_health_view

import io.axoniq.build.caretrack.patient_health_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Patient Health View component that handles patient health dashboard and information queries.
 * This component maintains a read model of patient health data by listening to health-related events
 * and provides query handlers for retrieving patient health information.
 */
@Component
class PatientHealthViewComponent(
    private val patientHealthRepository: PatientHealthRepository,
    private val appointmentRepository: AppointmentRepository,
    private val treatmentRepository: TreatmentRepository,
    private val diagnosisRepository: DiagnosisRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(PatientHealthViewComponent::class.java)
    }

    /**
     * Handles DetailedHealthInformation query to retrieve detailed health information for a specific patient and health area.
     * Returns detailed health information based on the patient ID and health area.
     */
    @QueryHandler
    fun handle(query: DetailedHealthInformation): DetailedHealthInformationResult {
        logger.info("Handling DetailedHealthInformation query for patient: ${query.patientId}, health area: ${query.healthArea}")

        val patient = patientHealthRepository.findById(query.patientId).orElse(null)
        
        val detailedInfo = when (query.healthArea.lowercase()) {
            "treatments" -> {
                val treatments = patient?.activeTreatments ?: emptyList()
                treatments.joinToString("\n") { "Medication: ${it.medicationName}, Dosage: ${it.dosage}, Frequency: ${it.frequency}, Duration: ${it.duration}" }
            }
            "appointments" -> {
                val appointments = patient?.upcomingAppointments ?: emptyList()
                appointments.joinToString("\n") { "Doctor: ${it.doctorName}, Date: ${it.appointmentDate}" }
            }
            "diagnoses" -> {
                val diagnoses = patient?.recentDiagnoses ?: emptyList()
                diagnoses.joinToString("\n") { "Condition: ${it.condition}, Date: ${it.diagnosisDate}, Severity: ${it.severity}" }
            }
            else -> "No detailed information available for health area: ${query.healthArea}"
        }

        return DetailedHealthInformationResult(
            detailedInfo = detailedInfo,
            healthArea = query.healthArea
        )
    }

    /**
     * Handles PersonalHealthDashboard query to retrieve a comprehensive health dashboard for a patient.
     * Returns the patient's name, active treatments, upcoming appointments, and recent diagnoses.
     */
    @QueryHandler
    fun handle(query: PersonalHealthDashboard): PersonalHealthDashboardResult {
        logger.info("Handling PersonalHealthDashboard query for patient: ${query.patientId}")

        val patient = patientHealthRepository.findById(query.patientId).orElse(null)

        if (patient == null) {
            logger.warn("Patient not found: ${query.patientId}")
            return PersonalHealthDashboardResult(
                patientName = "Unknown Patient",
                activeTreatments = emptyList(),
                upcomingAppointments = emptyList(),
                recentDiagnoses = emptyList()
            )
        }

        val treatmentSummaries = patient.activeTreatments.map { treatment ->
            TreatmentSummary(medicationName = treatment.medicationName)
        }

        val appointmentSummaries = patient.upcomingAppointments.map { appointment ->
            AppointmentSummary(
                doctorName = appointment.doctorName,
                appointmentDate = appointment.appointmentDate
            )
        }

        val diagnosisSummaries = patient.recentDiagnoses.map { diagnosis ->
            DiagnosisSummary(
                condition = diagnosis.condition,
                diagnosisDate = diagnosis.diagnosisDate
            )
        }

        return PersonalHealthDashboardResult(
            patientName = patient.patientName,
            activeTreatments = treatmentSummaries,
            upcomingAppointments = appointmentSummaries,
            recentDiagnoses = diagnosisSummaries
        )
    }

    /**
     * Handles AppointmentScheduled event to add a new appointment to the patient's health view.
     * Creates a new appointment entry for the patient.
     */
    @EventHandler
    fun on(event: AppointmentScheduled) {
        logger.info("Handling AppointmentScheduled event for patient: ${event.patientId}, appointment: ${event.appointmentId}")

        var patient = patientHealthRepository.findById(event.patientId).orElse(null)
        if (patient == null) {
            patient = PatientHealthEntity(patientId = event.patientId, patientName = "Patient ${event.patientId}")
            patient = patientHealthRepository.save(patient)
        }

        val appointment = AppointmentEntity(
            appointmentId = event.appointmentId,
            doctorName = "Dr. ${event.doctorId}",
            appointmentDate = event.appointmentDate,
            patient = patient
        )

        appointmentRepository.save(appointment)
        logger.info("Appointment scheduled for patient: ${event.patientId}")
    }

    /**
     * Handles AppointmentCancelled event to remove a cancelled appointment from the patient's health view.
     * Removes the appointment entry from the system.
     */
    @EventHandler
    fun on(event: AppointmentCancelled) {
        logger.info("Handling AppointmentCancelled event for appointment: ${event.appointmentId}")

        appointmentRepository.deleteById(event.appointmentId)
        logger.info("Appointment cancelled: ${event.appointmentId}")
    }

    /**
     * Handles TreatmentPrescribed event to add a new treatment to the patient's active treatments.
     * Creates a new treatment entry for the patient.
     */
    @EventHandler
    fun on(event: TreatmentPrescribed) {
        logger.info("Handling TreatmentPrescribed event for patient: ${event.patientId}, treatment: ${event.treatmentId}")

        var patient = patientHealthRepository.findById(event.patientId).orElse(null)
        if (patient == null) {
            patient = PatientHealthEntity(patientId = event.patientId, patientName = "Patient ${event.patientId}")
            patient = patientHealthRepository.save(patient)
        }

        val treatment = TreatmentEntity(
            treatmentId = event.treatmentId,
            medicationName = event.medicationName,
            dosage = event.dosage,
            frequency = event.frequency,
            duration = event.duration,
            patient = patient
        )

        treatmentRepository.save(treatment)
        logger.info("Treatment prescribed for patient: ${event.patientId}")
    }

    /**
     * Handles TreatmentDiscontinued event to remove a discontinued treatment from the patient's active treatments.
     * Removes the treatment entry from the active treatments.
     */
    @EventHandler
    fun on(event: TreatmentDiscontinued) {
        logger.info("Handling TreatmentDiscontinued event for patient: ${event.patientId}, treatment: ${event.treatmentId}")

        treatmentRepository.deleteById(event.treatmentId)
        logger.info("Treatment discontinued for patient: ${event.patientId}")
    }

    /**
     * Handles PatientDiagnosisRecorded event to add a new diagnosis to the patient's recent diagnoses.
     * Creates a new diagnosis entry for the patient.
     */
    @EventHandler
    fun on(event: PatientDiagnosisRecorded) {
        logger.info("Handling PatientDiagnosisRecorded event for patient: ${event.patientId}, diagnosis: ${event.diagnosisId}")

        var patient = patientHealthRepository.findById(event.patientId).orElse(null)
        if (patient == null) {
            patient = PatientHealthEntity(patientId = event.patientId, patientName = "Patient ${event.patientId}")
            patient = patientHealthRepository.save(patient)
        }
        
        val diagnosis = DiagnosisEntity(
            diagnosisId = event.diagnosisId,
            condition = event.condition,
            diagnosisDate = event.diagnosisDate,
            severity = event.severity,
            notes = event.notes,
            patient = patient
        )
        
        diagnosisRepository.save(diagnosis)
        logger.info("Diagnosis recorded for patient: ${event.patientId}")
    }

    /**
     * Handles PatientDiagnosisRemoved event to remove a diagnosis from the patient's recent diagnoses.
     * Removes the diagnosis entry from the system.
     */
    @EventHandler
    fun on(event: PatientDiagnosisRemoved) {
        logger.info("Handling PatientDiagnosisRemoved event for patient: ${event.patientId}, diagnosis: ${event.diagnosisId}")

        diagnosisRepository.deleteById(event.diagnosisId)
        logger.info("Diagnosis removed for patient: ${event.patientId}")
    }
}

