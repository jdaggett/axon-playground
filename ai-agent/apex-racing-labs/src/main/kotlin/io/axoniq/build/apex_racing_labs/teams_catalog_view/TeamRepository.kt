package io.axoniq.build.apex_racing_labs.teams_catalog_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

/**
 * JPA repository for the Team entity in the Teams Catalog View component.
 * Provides data access operations for team information.
 */
@Repository
interface TeamRepository : JpaRepository<Team, String> {
    
    /**
     * Finds all active teams for the available teams query.
     * @return List of active teams
     */
    @Query("SELECT t FROM Team t WHERE t.active = true")
    fun findAllActiveTeams(): List<Team>
}

