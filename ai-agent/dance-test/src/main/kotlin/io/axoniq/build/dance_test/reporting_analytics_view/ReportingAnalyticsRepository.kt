package io.axoniq.build.dance_test.reporting_analytics_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Repository interface for accessing ReportingAnalyticsEntity data.
 * Extends JpaRepository to provide standard CRUD operations for the Reporting Analytics View.
 */
@Repository
interface ReportingAnalyticsRepository : JpaRepository<ReportingAnalyticsEntity, String> {

    /**
     * Finds reporting analytics by instructor ID.
     * @param instructorId The instructor ID to search for
     * @return Optional containing the analytics entity if found
     */
    override fun findById(instructorId: String): Optional<ReportingAnalyticsEntity>
}

