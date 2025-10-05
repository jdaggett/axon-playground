package io.axoniq.build.dance_test.instructor_profile_view

import jakarta.persistence.*
import java.time.LocalDate

/**
 * JPA entity for lesson package data storage.
 * Stores lesson package information for the Instructor Profile View component.
 */
@Entity
@Table(name = "lesson_packages")
data class LessonPackageEntity(
    @Id
    val packageId: String,

    @Column(nullable = false)
    val lessonCount: Int,

    @Column(nullable = false)
    val lessonDuration: Int,

    @Column(nullable = false)
    val isActive: Boolean = true,

    @Column(nullable = false)
    val studentId: String,

    @Column(nullable = false)
    val creationDate: LocalDate,
    
    @Column(nullable = false)
    val price: Double,

    @Column(nullable = false)
    val instructorId: String
) {
    constructor() : this("", 0, 0, true, "", LocalDate.now(), 0.0, "")
}

