package io.axoniq.build.caretrack.patient_health_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * JPA repository for the PatientHealthEntity in the Patient Health View component.
 * Provides data access methods for patient health information.
 */
@Repository
interface PatientHealthRepository : JpaRepository<PatientHealthEntity, String>

/**
 * JPA repository for the AppointmentEntity in the Patient Health View component.
 * Provides data access methods for appointment information.
 */
@Repository
interface AppointmentRepository : JpaRepository<AppointmentEntity, String>

/**
 * JPA repository for the TreatmentEntity in the Patient Health View component.
 * Provides data access methods for treatment information.
 */
@Repository
interface TreatmentRepository : JpaRepository<TreatmentEntity, String>

/**
 * JPA repository for the DiagnosisEntity in the Patient Health View component.
 * Provides data access methods for diagnosis information.
 */
@Repository
interface DiagnosisRepository : JpaRepository<DiagnosisEntity, String>

