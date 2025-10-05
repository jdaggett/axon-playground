package io.axoniq.build.caretrack.appointment_view

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA entity representing an appointment in the appointment view.
 * This entity stores appointment information for query purposes.
 */
@Entity
@Table(name = "appointment")
data class AppointmentEntity(
    @Id
    val appointmentId: String,
    
    val patientId: String,
    
    val doctorId: String,

    val purpose: String,

    val appointmentDate: LocalDateTime,

    val status: String,
    
    // Additional fields for denormalized view
    var patientName: String? = null,

    var doctorName: String? = null
) {
    // JPA requires a no-arg constructor
    constructor() : this("", "", "", "", LocalDateTime.now(), "")
}

