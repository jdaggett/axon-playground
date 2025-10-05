package io.axoniq.build.dance_test.session_calendar_view

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * JPA entity representing a dance session in the Session Calendar View component.
 * This entity stores session information for querying and calendar display purposes.
 */
@Entity
@Table(name = "sessions")
data class SessionEntity(
    @Id
    val sessionId: String,
    
    val instructorId: String,
    
    val duration: Int,

    val sessionDate: LocalDateTime,

    val studentId: String,

    val notes: String? = null,

    val status: String,

    val studentName: String,

    val actualDuration: Int? = null
) {
    // JPA requires a no-arg constructor
    constructor() : this("", "", 0, LocalDateTime.now(), "", null, "", "", null)
}

