package io.axoniq.build.caretrack.medical_history_view

import jakarta.persistence.*

/**
 * JPA entity representing a patient treatment in the medical history view.
 * This entity stores treatment information for the Medical History View component.
 */
@Entity
@Table(name = "treatments")
data class TreatmentEntity(
    @Id
    val treatmentId: String = "",

    @Column(nullable = false)
    val patientId: String = "",

    @Column(nullable = false)
    val medicationName: String = "",

    @Column(nullable = false)
    val dosage: String = "",
    
    @Column(nullable = false)
    val frequency: String = "",

    @Column(nullable = false)
    val duration: String = "",

    @Column(nullable = false)
    val doctorId: String = "",
    
    @Column(nullable = false)
    val status: String = "ACTIVE"
)

