package io.axoniq.build.caretrack.family_health_view

import io.axoniq.build.caretrack.family_health_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Family Health View Component - handles family member access to permitted patient health information.
 * This query component maintains a read model of family member permissions and permitted health data.
 */
@Component
class FamilyHealthViewComponent(
    private val familyHealthPermissionRepository: FamilyHealthPermissionRepository,
    private val permittedDiagnosisRepository: PermittedDiagnosisRepository,
    private val permittedTreatmentRepository: PermittedTreatmentRepository,
    private val permittedAppointmentRepository: PermittedAppointmentRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(FamilyHealthViewComponent::class.java)
    }

    /**
     * Query handler for retrieving detailed permitted health data.
     * Part of the Family Health View component for detailed health information access.
     */
    @QueryHandler
    fun handle(query: DetailedPermittedHealthData): DetailedPermittedHealthDataResult? {
        logger.info("Handling DetailedPermittedHealthData query for patient: ${query.patientId}, family member: ${query.familyMemberEmail}, health area: ${query.healthArea}")

        val permission = familyHealthPermissionRepository
            .findByPatientIdAndFamilyMemberEmail(query.patientId, query.familyMemberEmail)
            ?: return null

        val detailedInfo = when (query.healthArea.lowercase()) {
            "diagnosis", "diagnoses" -> {
                permission.permittedDiagnoses
                    .joinToString(", ") { "${it.condition} (${it.diagnosisDate})" }
            }
            "treatment", "treatments" -> {
                permission.permittedTreatments
                    .joinToString(", ") { "${it.medicationName} - ${it.dosage} ${it.frequency}" }
            }
            "appointment", "appointments" -> {
                permission.permittedAppointments
                    .joinToString(", ") { "${it.doctorName} on ${it.appointmentDate}" }
            }
            else -> "No detailed information available for health area: ${query.healthArea}"
        }

        return DetailedPermittedHealthDataResult(
            detailedInfo = detailedInfo,
            healthArea = query.healthArea
        )
    }
    
    /**
     * Query handler for retrieving permitted patient health information.
     * Part of the Family Health View component for comprehensive health information access.
     */
    @QueryHandler
    fun handle(query: PermittedPatientHealthInfo): PermittedPatientHealthInfoResult? {
        logger.info("Handling PermittedPatientHealthInfo query for patient: ${query.patientId}, family member: ${query.familyMemberEmail}")

        val permission = familyHealthPermissionRepository
            .findByPatientIdAndFamilyMemberEmail(query.patientId, query.familyMemberEmail)
            ?: return null
        
        val permittedDiagnoses = permission.permittedDiagnoses.map { diagnosis ->
            DiagnosisSummary(
                condition = diagnosis.condition,
                diagnosisDate = diagnosis.diagnosisDate
            )
        }
        
        val permittedTreatments = permission.permittedTreatments.map { treatment ->
            TreatmentSummary(
                medicationName = treatment.medicationName
            )
        }
        
        val permittedAppointments = permission.permittedAppointments.map { appointment ->
            AppointmentSummary(
                doctorName = appointment.doctorName,
                appointmentDate = appointment.appointmentDate
            )
        }

        return PermittedPatientHealthInfoResult(
            permittedDiagnoses = permittedDiagnoses,
            permittedTreatments = permittedTreatments,
            patientName = permission.patientName,
            permittedAppointments = permittedAppointments
        )
    }

    /**
     * Event handler for family member permissions changed events.
     * Updates the access level for existing family member permissions in the Family Health View.
     */
    @EventHandler
    fun on(event: FamilyMemberPermissionsChanged) {
        logger.info("Handling FamilyMemberPermissionsChanged event for patient: ${event.patientId}, family member: ${event.familyMemberEmail}")
        
        val permission = familyHealthPermissionRepository
            .findByPatientIdAndFamilyMemberEmail(event.patientId, event.familyMemberEmail)
        
        if (permission != null) {
            val updatedPermission = permission.copy(accessLevel = event.newAccessLevel)
            familyHealthPermissionRepository.save(updatedPermission)
            logger.info("Updated access level to ${event.newAccessLevel} for family member: ${event.familyMemberEmail}")
        } else {
            logger.warn("No existing permission found for patient: ${event.patientId}, family member: ${event.familyMemberEmail}")
        }
    }

    /**
     * Event handler for family member access granted events.
     * Creates new family member permission entries in the Family Health View.
     */
    @EventHandler
    fun on(event: FamilyMemberAccessGranted) {
        logger.info("Handling FamilyMemberAccessGranted event for patient: ${event.patientId}, family member: ${event.familyMemberEmail}")

        val existingPermission = familyHealthPermissionRepository
            .findByPatientIdAndFamilyMemberEmail(event.patientId, event.familyMemberEmail)

        if (existingPermission == null) {
            val newPermission = FamilyHealthPermission(
                patientId = event.patientId,
                familyMemberEmail = event.familyMemberEmail,
                accessLevel = event.accessLevel
            )
            familyHealthPermissionRepository.save(newPermission)
            logger.info("Created new permission for family member: ${event.familyMemberEmail} with access level: ${event.accessLevel}")
        } else {
            val updatedPermission = existingPermission.copy(accessLevel = event.accessLevel)
            familyHealthPermissionRepository.save(updatedPermission)
            logger.info("Updated existing permission for family member: ${event.familyMemberEmail} with access level: ${event.accessLevel}")
        }
    }

    /**
     * Event handler for treatment prescribed events.
     * Adds permitted treatment information to the Family Health View for authorized family members.
     */
    @EventHandler
    fun on(event: TreatmentPrescribed) {
        logger.info("Handling TreatmentPrescribed event for patient: ${event.patientId}, treatment: ${event.treatmentId}")

        val permissions = familyHealthPermissionRepository.findAll()
            .filter { it.patientId == event.patientId }
        
        permissions.forEach { permission ->
            val existingTreatment = permittedTreatmentRepository
                .findByTreatmentIdAndPermission(event.treatmentId, permission)
            
            if (existingTreatment == null) {
                val newTreatment = PermittedTreatment(
                    treatmentId = event.treatmentId,
                    medicationName = event.medicationName,
                    dosage = event.dosage,
                    frequency = event.frequency,
                    duration = event.duration,
                    permission = permission
                )
                permittedTreatmentRepository.save(newTreatment)
                logger.info("Added treatment ${event.medicationName} to permissions for family member: ${permission.familyMemberEmail}")
            }
        }
    }
    
    /**
     * Event handler for patient diagnosis recorded events.
     * Adds permitted diagnosis information to the Family Health View for authorized family members.
     */
    @EventHandler
    fun on(event: PatientDiagnosisRecorded) {
        logger.info("Handling PatientDiagnosisRecorded event for patient: ${event.patientId}, diagnosis: ${event.diagnosisId}")

        val permissions = familyHealthPermissionRepository.findAll()
            .filter { it.patientId == event.patientId }

        permissions.forEach { permission ->
            val existingDiagnosis = permittedDiagnosisRepository
                .findByDiagnosisIdAndPermission(event.diagnosisId, permission)

            if (existingDiagnosis == null) {
                val newDiagnosis = PermittedDiagnosis(
                    diagnosisId = event.diagnosisId,
                    condition = event.condition,
                    diagnosisDate = event.diagnosisDate,
                    severity = event.severity,
                    notes = event.notes,
                    permission = permission
                )
                permittedDiagnosisRepository.save(newDiagnosis)
                logger.info("Added diagnosis ${event.condition} to permissions for family member: ${permission.familyMemberEmail}")
            }
        }
    }

    /**
     * Event handler for appointment scheduled events.
     * Adds permitted appointment information to the Family Health View for authorized family members.
     */
    @EventHandler
    fun on(event: AppointmentScheduled) {
        logger.info("Handling AppointmentScheduled event for patient: ${event.patientId}, appointment: ${event.appointmentId}")

        val permissions = familyHealthPermissionRepository.findAll()
            .filter { it.patientId == event.patientId }
        
        permissions.forEach { permission ->
            val existingAppointment = permittedAppointmentRepository
                .findByAppointmentIdAndPermission(event.appointmentId, permission)

            if (existingAppointment == null) {
                val newAppointment = PermittedAppointment(
                    appointmentId = event.appointmentId,
                    doctorName = "Doctor ${event.doctorId}", // Using doctorId as placeholder for doctor name
                    appointmentDate = event.appointmentDate,
                    purpose = event.purpose,
                    permission = permission
                )
                permittedAppointmentRepository.save(newAppointment)
                logger.info("Added appointment with ${newAppointment.doctorName} to permissions for family member: ${permission.familyMemberEmail}")
            }
        }
    }
}

