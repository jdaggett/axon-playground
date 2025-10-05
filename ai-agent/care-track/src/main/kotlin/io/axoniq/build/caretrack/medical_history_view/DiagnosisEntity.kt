package io.axoniq.build.caretrack.medical_history_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing a patient diagnosis in the medical history view.
 * This entity stores diagnosis information for the Medical History View component.
 */
@Entity
@Table(name = "diagnoses")
data class DiagnosisEntity(
    @Id
    val diagnosisId: String = "",

    @Column(nullable = false)
    val patientId: String = "",

    @Column(nullable = false)
    val condition: String = "",

    @Column(nullable = false)
    val severity: String = "",

    @Column(nullable = false)
    val diagnosisDate: LocalDate = LocalDate.now(),

    @Column(nullable = false)
    val doctorId: String = "",
    
    @Column(nullable = true)
    val notes: String? = null
)

