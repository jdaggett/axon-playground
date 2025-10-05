package io.axoniq.build.dance_test.reporting_analytics_view

import io.axoniq.build.dance_test.reporting_analytics_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.WeekFields
import java.util.*

/**
 * View component for Reporting Analytics.
 * Handles queries for business analytics and reporting views for instructors.
 * Maintains read models through event handlers and responds to analytical queries.
 */
@Component
class ReportingAnalyticsViewComponent(
    private val repository: ReportingAnalyticsRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ReportingAnalyticsViewComponent::class.java)
    }

    /**
     * Query handler for StudentCountTrends query.
     * Returns student count trends data for the specified instructor and period.
     */
    @QueryHandler
    fun handle(query: StudentCountTrends): StudentCountData {
        logger.info("Handling StudentCountTrends query for instructor: ${query.instructorId}, period: ${query.periodMonths} months")

        // For demonstration purposes, creating mock trend data
        // In a real implementation, this would calculate actual trends from historical data
        val monthlyStudentCounts = generateMonthlyStudentCounts(query.periodMonths)
        val growthTrend = calculateGrowthTrend(monthlyStudentCounts)

        return StudentCountData(
            monthlyStudentCounts = monthlyStudentCounts,
            growthTrend = growthTrend
        )
    }

    /**
     * Query handler for StudentRetentionAnalytics query.
     * Returns student retention analytics data for the specified instructor and period.
     */
    @QueryHandler
    fun handle(query: StudentRetentionAnalytics): StudentRetentionData {
        logger.info("Handling StudentRetentionAnalytics query for instructor: ${query.instructorId}, period: ${query.periodMonths} months")

        val analytics = repository.findById(query.instructorId)

        return if (analytics.isPresent) {
            val entity = analytics.get()
            StudentRetentionData(
                newStudents = 15, // Mock data - would be calculated from historical events
                averageSessionsPerStudent = entity.averageSessionsPerWeek.toDouble() * 4,
                retentionRate = entity.studentRetentionRate.toDouble(),
                churredStudents = 3, // Mock data
                activeStudents = entity.totalActiveStudents
            )
        } else {
            StudentRetentionData(
                newStudents = 0,
                averageSessionsPerStudent = 0.0,
                retentionRate = 0.0,
                churredStudents = 0,
                activeStudents = 0
            )
        }
    }

    /**
     * Query handler for WorkloadAnalysis query.
     * Returns workload analysis data for the specified instructor and period.
     */
    @QueryHandler
    fun handle(query: WorkloadAnalysis): WorkloadAnalysisData {
        logger.info("Handling WorkloadAnalysis query for instructor: ${query.instructorId}, period: ${query.periodWeeks} weeks")

        val analytics = repository.findById(query.instructorId)

        return if (analytics.isPresent) {
            val entity = analytics.get()
            WorkloadAnalysisData(
                capacityUtilization = 0.75, // Mock calculation
                averageSessionsPerDay = entity.averageSessionsPerWeek.toDouble() / 7,
                totalSessionsPerWeek = entity.averageSessionsPerWeek.toDouble(),
                peakDays = listOf("Tuesday", "Thursday", "Saturday"),
                availableCapacity = 20 // Mock data
            )
        } else {
            WorkloadAnalysisData(
                capacityUtilization = 0.0,
                averageSessionsPerDay = 0.0,
                totalSessionsPerWeek = 0.0,
                peakDays = emptyList(),
                availableCapacity = 0
            )
        }
    }

    /**
     * Query handler for YearlyRevenueComparison query.
     * Returns yearly revenue comparison data for the specified instructor and year range.
     */
    @QueryHandler
    fun handle(query: YearlyRevenueComparison): YearlyRevenueData {
        logger.info("Handling YearlyRevenueComparison query for instructor: ${query.instructorId}, years: ${query.startYear}-${query.endYear}")

        val yearlyComparisons = generateYearlyComparisons(query.startYear, query.endYear)
        val growthRate = calculateYearOverYearGrowth(yearlyComparisons)

        return YearlyRevenueData(
            yearlyComparisons = yearlyComparisons,
            growthRate = growthRate
        )
    }

    /**
     * Query handler for MonthlyRevenueReport query.
     * Returns monthly revenue report data for the specified instructor, month and year.
     */
    @QueryHandler
    fun handle(query: MonthlyRevenueReport): MonthlyRevenueData {
        logger.info("Handling MonthlyRevenueReport query for instructor: ${query.instructorId}, month: ${query.month}, year: ${query.year}")

        val analytics = repository.findById(query.instructorId)

        return if (analytics.isPresent) {
            val entity = analytics.get()
            MonthlyRevenueData(
                revenueByPaymentMethod = listOf(
                    PaymentMethodRevenue("Credit Card", entity.totalMonthlyRevenue.toDouble() * 0.6),
                    PaymentMethodRevenue("Cash", entity.totalMonthlyRevenue.toDouble() * 0.3),
                    PaymentMethodRevenue("Bank Transfer", entity.totalMonthlyRevenue.toDouble() * 0.1)
                ),
                totalRevenue = entity.totalMonthlyRevenue.toDouble(),
                totalTransactions = entity.totalSessionsThisMonth,
                averageTransactionValue = if (entity.totalSessionsThisMonth > 0) entity.totalMonthlyRevenue.toDouble() / entity.totalSessionsThisMonth else 0.0
            )
        } else {
            MonthlyRevenueData(
                revenueByPaymentMethod = emptyList(),
                totalRevenue = 0.0,
                totalTransactions = 0,
                averageTransactionValue = 0.0
            )
        }
    }

    /**
     * Query handler for WeeklySessionPatterns query.
     * Returns weekly session patterns data for the specified instructor and period.
     */
    @QueryHandler
    fun handle(query: WeeklySessionPatterns): WeeklySessionData {
        logger.info("Handling WeeklySessionPatterns query for instructor: ${query.instructorId}, period: ${query.periodWeeks} weeks")

        val analytics = repository.findById(query.instructorId)
        val totalWeeklySessions = analytics.map { it.averageSessionsPerWeek.toInt() }.orElse(0)

        val weeklyPatterns = listOf(
            WeeklyPattern(2.5, "Monday", listOf("18:00", "19:00")),
            WeeklyPattern(3.0, "Tuesday", listOf("17:00", "18:00", "19:00")),
            WeeklyPattern(2.0, "Wednesday", listOf("18:00", "19:00")),
            WeeklyPattern(3.5, "Thursday", listOf("17:00", "18:00", "19:00", "20:00")),
            WeeklyPattern(1.5, "Friday", listOf("18:00")),
            WeeklyPattern(4.0, "Saturday", listOf("10:00", "11:00", "14:00", "15:00")),
            WeeklyPattern(1.0, "Sunday", listOf("15:00"))
        )

        return WeeklySessionData(
            totalWeeklySessions = totalWeeklySessions,
            weeklyPatterns = weeklyPatterns
        )
    }

    /**
     * Event handler for SessionCompleted events.
     * Updates analytics when a session is completed.
     */
    @EventHandler
    fun on(event: SessionCompleted) {
        logger.info("Handling SessionCompleted event for session: ${event.sessionId}")
        // Implementation would update session completion metrics
        // This is a placeholder for actual analytics calculation logic
    }

    /**
     * Event handler for StudentProfileCreated events.
     * Updates student count analytics when a new student profile is created.
     */
    @EventHandler
    fun on(event: StudentProfileCreated) {
        logger.info("Handling StudentProfileCreated event for student: ${event.studentId}, instructor: ${event.instructorId}")
        val existing = repository.findById(event.instructorId)
        val analytics = if (existing.isPresent) {
            val current = existing.get()
            current.copy(totalActiveStudents = current.totalActiveStudents + 1)
        } else {
            ReportingAnalyticsEntity(
                instructorId = event.instructorId,
                totalActiveStudents = 1
            )
        }

        repository.save(analytics)
    }

    /**
     * Event handler for SessionScheduled events.
     * Updates session-related analytics when a session is scheduled.
     */
    @EventHandler
    fun on(event: SessionScheduled) {
        logger.info("Handling SessionScheduled event for session: ${event.sessionId}, instructor: ${event.instructorId}")

        val existing = repository.findById(event.instructorId)
        val analytics = if (existing.isPresent) {
            val current = existing.get()
            val currentMonth = LocalDate.now().monthValue
            val sessionMonth = event.sessionDate.monthValue

            if (currentMonth == sessionMonth) {
                current.copy(totalSessionsThisMonth = current.totalSessionsThisMonth + 1)
            } else {
                current
            }
        } else {
            ReportingAnalyticsEntity(
                instructorId = event.instructorId,
                totalSessionsThisMonth = 1
            )
        }

        repository.save(analytics)
    }

    /**
     * Event handler for TransactionRecordCreated events.
     * Updates financial analytics when a transaction is recorded.
     */
    @EventHandler
    fun on(event: TransactionRecordCreated) {
        logger.info("Handling TransactionRecordCreated event for student: ${event.studentId}, amount: ${event.amount}")
        // Implementation would update transaction-related metrics
        // This is a placeholder for actual analytics calculation logic
    }

    /**
     * Event handler for PaymentRecorded events.
     * Updates revenue analytics when a payment is recorded.
     */
    @EventHandler
    fun on(event: PaymentRecorded) {
        logger.info("Handling PaymentRecorded event for student: ${event.studentId}, amount: ${event.amount}")
        // Implementation would update payment and revenue metrics
        // This is a placeholder for actual analytics calculation logic
    }

    /**
     * Helper method to generate monthly student count data for trend analysis.
     */
    private fun generateMonthlyStudentCounts(periodMonths: Int): List<MonthlyStudentCount> {
        val counts = mutableListOf<MonthlyStudentCount>()
        val now = LocalDate.now()

        for (i in 0 until periodMonths) {
            val month = now.minusMonths(i.toLong())
            counts.add(
                MonthlyStudentCount(
                    (5..15).random(),
                    month.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                    (20..50).random()
                )
            )
        }

        return counts.reversed()
    }

    /**
     * Helper method to calculate growth trend from monthly student counts.
     */
    private fun calculateGrowthTrend(monthlyStudentCounts: List<MonthlyStudentCount>): String {
        if (monthlyStudentCounts.size < 2) return "Insufficient Data"

        val firstMonth = monthlyStudentCounts.first()
        val lastMonth = monthlyStudentCounts.last()

        return when {
            lastMonth.activeStudents > firstMonth.activeStudents -> "Growing"
            lastMonth.activeStudents < firstMonth.activeStudents -> "Declining"
            else -> "Stable"
        }
    }

    /**
     * Helper method to generate yearly revenue comparisons.
     */
    private fun generateYearlyComparisons(startYear: Int, endYear: Int): List<YearlyRevenue> {
        val comparisons = mutableListOf<YearlyRevenue>()

        for (year in startYear..endYear) {
            val monthlyBreakdown = mutableListOf<MonthlyRevenue>()
            var totalRevenue = 0.0

            for (month in 1..12) {
                val revenue = (1000..5000).random().toDouble()
                monthlyBreakdown.add(MonthlyRevenue(revenue, month))
                totalRevenue += revenue
            }

            comparisons.add(
                YearlyRevenue(
                    monthlyBreakdown = monthlyBreakdown,
                    year = year,
                    totalRevenue = totalRevenue
                )
            )
        }

        return comparisons
    }

    /**
     * Helper method to calculate year-over-year growth rate.
     */
    private fun calculateYearOverYearGrowth(yearlyComparisons: List<YearlyRevenue>): Double {
        if (yearlyComparisons.size < 2) return 0.0

        val currentYear = yearlyComparisons.last()
        val previousYear = yearlyComparisons[yearlyComparisons.size - 2]

        return if (previousYear.totalRevenue > 0) {
            ((currentYear.totalRevenue - previousYear.totalRevenue) / previousYear.totalRevenue) * 100
        } else {
            0.0
        }
    }
}