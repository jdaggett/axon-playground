package io.axoniq.build.apex_racing_labs.race_list_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

/**
 * Repository interface for accessing race data in the Race List View component.
 * Provides methods for querying race information and searching races.
 */
@Repository
interface RaceRepository : JpaRepository<RaceEntity, String> {

    /**
     * Finds races by searching track names that contain the given search term (case-insensitive).
     * Used for race search functionality.
     */
    @Query("SELECT r FROM RaceEntity r WHERE LOWER(r.trackName) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    fun findByTrackNameContainingIgnoreCase(@Param("searchTerm") searchTerm: String): List<RaceEntity>

    /**
     * Finds all races ordered by race date descending (most recent first).
     * Used for displaying chronological race listings.
     */
    override fun findAll(): List<RaceEntity>

    @Query("SELECT r FROM RaceEntity r ORDER BY r.raceDate DESC")
    fun findAllOrderByRaceDateDesc(): List<RaceEntity>
}

