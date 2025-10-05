package io.axoniq.build.dance_test.student_roster_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity representing a student in the Student Roster View component.
 * This entity stores all student information needed for roster and detailed views.
 */
@Entity
@Table(name = "student_roster")
data class StudentRosterEntity(
    @Id
    @Column(name = "student_id")
    val studentId: String = "",

    @Column(name = "instructor_id", nullable = false)
    val instructorId: String = "",
    
    @Column(name = "name", nullable = false)
    val name: String = "",
    
    @Column(name = "phone", nullable = false)
    val phone: String = "",
    
    @Column(name = "monetary_balance", nullable = false)
    val monetaryBalance: Double = 0.0,

    @Column(name = "lesson_balance", nullable = false)
    val lessonBalance: Int = 0,

    @Column(name = "relationship_status", nullable = false)
    val relationshipStatus: String = "",

    @Column(name = "booking_access_status", nullable = false)
    val bookingAccessStatus: String = "",

    @Column(name = "last_booking_date")
    val lastBookingDate: LocalDate? = null,
    
    @Column(name = "total_sessions_completed", nullable = false)
    val totalSessionsCompleted: Int = 0,

    @Column(name = "total_lifetime_payments", nullable = false)
    val totalLifetimePayments: Double = 0.0
)

