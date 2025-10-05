package io.axoniq.build.dance_test.instructor_profile_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for lesson package entities.
 * Provides data access operations for the Instructor Profile View component.
 */
@Repository
interface LessonPackageRepository : JpaRepository<LessonPackageEntity, String>

