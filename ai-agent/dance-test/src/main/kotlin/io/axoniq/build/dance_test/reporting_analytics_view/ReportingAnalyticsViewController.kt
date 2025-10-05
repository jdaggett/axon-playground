package io.axoniq.build.dance_test.reporting_analytics_view

import io.axoniq.build.dance_test.reporting_analytics_view.api.*
import org.axonframework.queryhandling.QueryGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*
import java.util.concurrent.CompletableFuture

/**
 * REST Controller for Reporting Analytics View.
 * Provides HTTP endpoints for accessing business analytics and reporting data for instructors.
 */
@RestController
@RequestMapping("/api/reporting-analytics")
class ReportingAnalyticsViewController(
    private val queryGateway: QueryGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(ReportingAnalyticsViewController::class.java)
    }

    /**
     * GET endpoint for student count trends.
     * Returns student count trend data for the specified instructor and period.
     */
    @GetMapping("/student-count-trends/{instructorId}")
    fun getStudentCountTrends(
        @PathVariable instructorId: String,
        @RequestParam(defaultValue = "6") periodMonths: Int
    ): CompletableFuture<StudentCountData> {
        logger.info("REST request for student count trends - instructor: $instructorId, period: $periodMonths months")
        val query = StudentCountTrends(instructorId, periodMonths)
        return queryGateway.query(query, StudentCountData::class.java, null)
    }

    /**
     * GET endpoint for student retention analytics.
     * Returns student retention analytics for the specified instructor and period.
     */
    @GetMapping("/student-retention/{instructorId}")
    fun getStudentRetentionAnalytics(
        @PathVariable instructorId: String,
        @RequestParam(defaultValue = "12") periodMonths: Int
    ): CompletableFuture<StudentRetentionData> {
        logger.info("REST request for student retention analytics - instructor: $instructorId, period: $periodMonths months")
        val query = StudentRetentionAnalytics(instructorId, periodMonths)
        return queryGateway.query(query, StudentRetentionData::class.java, null)
    }

    /**
     * GET endpoint for workload analysis.
     * Returns workload analysis data for the specified instructor and period.
     */
    @GetMapping("/workload-analysis/{instructorId}")
    fun getWorkloadAnalysis(
        @PathVariable instructorId: String,
        @RequestParam(defaultValue = "4") periodWeeks: Int
    ): CompletableFuture<WorkloadAnalysisData> {
        logger.info("REST request for workload analysis - instructor: $instructorId, period: $periodWeeks weeks")
        val query = WorkloadAnalysis(instructorId, periodWeeks)
        return queryGateway.query(query, WorkloadAnalysisData::class.java, null)
    }

    /**
     * GET endpoint for yearly revenue comparison.
     * Returns yearly revenue comparison data for the specified instructor and year range.
     */
    @GetMapping("/yearly-revenue-comparison/{instructorId}")
    fun getYearlyRevenueComparison(
        @PathVariable instructorId: String,
        @RequestParam startYear: Int,
        @RequestParam endYear: Int
    ): CompletableFuture<YearlyRevenueData> {
        logger.info("REST request for yearly revenue comparison - instructor: $instructorId, years: $startYear-$endYear")
        val query = YearlyRevenueComparison(instructorId, endYear, startYear)
        return queryGateway.query(query, YearlyRevenueData::class.java, null)
    }

    /**
     * GET endpoint for monthly revenue report.
     * Returns monthly revenue report for the specified instructor, month and year.
     */
    @GetMapping("/monthly-revenue/{instructorId}")
    fun getMonthlyRevenueReport(
        @PathVariable instructorId: String,
        @RequestParam month: Int,
        @RequestParam year: Int
    ): CompletableFuture<MonthlyRevenueData> {
        logger.info("REST request for monthly revenue report - instructor: $instructorId, month: $month, year: $year")
        val query = MonthlyRevenueReport(instructorId, month, year)
        return queryGateway.query(query, MonthlyRevenueData::class.java, null)
    }

    /**
     * GET endpoint for weekly session patterns.
     * Returns weekly session patterns for the specified instructor and period.
     */
    @GetMapping("/weekly-session-patterns/{instructorId}")
    fun getWeeklySessionPatterns(
        @PathVariable instructorId: String,
        @RequestParam(defaultValue = "8") periodWeeks: Int
    ): CompletableFuture<WeeklySessionData> {
        logger.info("REST request for weekly session patterns - instructor: $instructorId, period: $periodWeeks weeks")
        val query = WeeklySessionPatterns(instructorId, periodWeeks)
        return queryGateway.query(query, WeeklySessionData::class.java, null)
    }
}