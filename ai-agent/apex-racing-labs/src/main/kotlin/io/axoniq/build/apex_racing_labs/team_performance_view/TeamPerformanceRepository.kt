package io.axoniq.build.apex_racing_labs.team_performance_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for the Team Performance Statistics View component.
 * Provides data access methods for team performance entities.
 */
@Repository
interface TeamPerformanceRepository : JpaRepository<TeamPerformanceEntity, String>

