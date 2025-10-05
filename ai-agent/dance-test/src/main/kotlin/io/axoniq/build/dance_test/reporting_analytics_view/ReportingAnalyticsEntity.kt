package io.axoniq.build.dance_test.reporting_analytics_view

import jakarta.persistence.*
import java.math.BigDecimal

/**
 * JPA Entity representing the Reporting Analytics View for instructors.
 * This entity stores aggregated analytics data for business reporting purposes.
 */
@Entity
@Table(name = "reporting_analytics")
data class ReportingAnalyticsEntity(
    @Id
    @Column(name = "instructor_id")
    val instructorId: String = "",

    @Column(name = "total_monthly_revenue", precision = 19, scale = 2)
    val totalMonthlyRevenue: BigDecimal = BigDecimal.ZERO,

    @Column(name = "year_over_year_growth", precision = 19, scale = 2)
    val yearOverYearGrowth: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_sessions_this_month")
    val totalSessionsThisMonth: Int = 0,

    @Column(name = "student_retention_rate", precision = 19, scale = 2)
    val studentRetentionRate: BigDecimal = BigDecimal.ZERO,

    @Column(name = "average_sessions_per_week", precision = 19, scale = 2)
    val averageSessionsPerWeek: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_active_students")
    val totalActiveStudents: Int = 0
)

