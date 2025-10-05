package io.axoniq.build.caretrack.patient_health_view

import io.axoniq.build.caretrack.patient_health_view.api.*
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA entity representing a patient's health information for the Patient Health View component.
 * This entity stores the patient's basic information, upcoming appointments, active treatments,
 * and recent diagnoses to support the personal health dashboard and detailed health information queries.
 */
@Entity
@Table(name = "patient_health")
data class PatientHealthEntity(
    @Id
    val patientId: String = "",

    val patientName: String = "",
    
    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val upcomingAppointments: MutableList<AppointmentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val activeTreatments: MutableList<TreatmentEntity> = mutableListOf(),

    @OneToMany(mappedBy = "patient", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val recentDiagnoses: MutableList<DiagnosisEntity> = mutableListOf()
)

/**
 * JPA entity representing an appointment for the Patient Health View component.
 * Stores appointment details including doctor name and appointment date.
 */
@Entity
@Table(name = "appointments")
data class AppointmentEntity(
    @Id
    val appointmentId: String = "",

    val doctorName: String = "",
    val appointmentDate: LocalDateTime = LocalDateTime.now(),

    @ManyToOne
    @JoinColumn(name = "patient_id")
    val patient: PatientHealthEntity? = null
)

/**
 * JPA entity representing a treatment for the Patient Health View component.
 * Stores treatment details including medication name and other treatment information.
 */
@Entity
@Table(name = "treatments")
data class TreatmentEntity(
    @Id
    val treatmentId: String = "",

    val medicationName: String = "",
    val dosage: String = "",
    val frequency: String = "",
    val duration: String = "",
    
    @ManyToOne
    @JoinColumn(name = "patient_id")
    val patient: PatientHealthEntity? = null
)

/**
 * JPA entity representing a diagnosis for the Patient Health View component.
 * Stores diagnosis details including condition and diagnosis date.
 */
@Entity
@Table(name = "diagnoses")
data class DiagnosisEntity(
    @Id
    val diagnosisId: String = "",

    val condition: String = "",
    val diagnosisDate: LocalDate = LocalDate.now(),
    val severity: String = "",
    val notes: String? = null,

    @ManyToOne
    @JoinColumn(name = "patient_id")
    val patient: PatientHealthEntity? = null
)

