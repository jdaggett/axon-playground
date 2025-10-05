package io.axoniq.build.caretrack.family_health_view

import io.axoniq.build.caretrack.family_health_view.api.*
import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA Entity representing family member health permissions and permitted health data.
 * Part of the Family Health View component for handling family member access to patient health information.
 */
@Entity
@Table(name = "family_health_permissions")
data class FamilyHealthPermission(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    
    @Column(name = "patient_id", nullable = false)
    val patientId: String,

    @Column(name = "family_member_email", nullable = false)
    val familyMemberEmail: String,

    @Column(name = "access_level", nullable = false)
    val accessLevel: String,

    @Column(name = "patient_name")
    val patientName: String = "",

    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val permittedDiagnoses: MutableList<PermittedDiagnosis> = mutableListOf(),

    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val permittedTreatments: MutableList<PermittedTreatment> = mutableListOf(),
    
    @OneToMany(mappedBy = "permission", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val permittedAppointments: MutableList<PermittedAppointment> = mutableListOf()
)

/**
 * JPA Entity representing permitted diagnosis information for family members.
 * Part of the Family Health View component.
 */
@Entity
@Table(name = "permitted_diagnoses")
data class PermittedDiagnosis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "diagnosis_id", nullable = false)
    val diagnosisId: String,

    @Column(name = "condition", nullable = false)
    val condition: String,

    @Column(name = "diagnosis_date", nullable = false)
    val diagnosisDate: LocalDate,

    @Column(name = "severity")
    val severity: String = "",

    @Column(name = "notes")
    val notes: String? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: FamilyHealthPermission? = null
)

/**
 * JPA Entity representing permitted treatment information for family members.
 * Part of the Family Health View component.
 */
@Entity
@Table(name = "permitted_treatments")
data class PermittedTreatment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "treatment_id", nullable = false)
    val treatmentId: String,

    @Column(name = "medication_name", nullable = false)
    val medicationName: String,

    @Column(name = "dosage")
    val dosage: String = "",

    @Column(name = "frequency")
    val frequency: String = "",

    @Column(name = "duration")
    val duration: String = "",
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: FamilyHealthPermission? = null
)

/**
 * JPA Entity representing permitted appointment information for family members.
 * Part of the Family Health View component.
 */
@Entity
@Table(name = "permitted_appointments")
data class PermittedAppointment(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "appointment_id", nullable = false)
    val appointmentId: String,

    @Column(name = "doctor_name", nullable = false)
    val doctorName: String,

    @Column(name = "appointment_date", nullable = false)
    val appointmentDate: LocalDateTime,

    @Column(name = "purpose")
    val purpose: String = "",

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "permission_id")
    val permission: FamilyHealthPermission? = null
)

