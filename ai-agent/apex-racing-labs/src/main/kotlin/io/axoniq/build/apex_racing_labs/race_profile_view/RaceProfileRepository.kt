package io.axoniq.build.apex_racing_labs.race_profile_view

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Repository interface for RaceProfileEntity in the Race Profile View component.
 * Provides data access operations for race profile information.
 */
@Repository
interface RaceProfileRepository : JpaRepository<RaceProfileEntity, String>

/**
 * Repository interface for UserCommentEntity in the Race Profile View component.
 * Provides data access operations for user comments and ratings.
 */
@Repository
interface UserCommentRepository : JpaRepository<UserCommentEntity, Long> {
    fun findByRaceId(raceId: String): List<UserCommentEntity>
    fun findByRaceIdAndUserId(raceId: String, userId: String): UserCommentEntity?
}

/**
 * Repository interface for DriverInfoEntity in the Race Profile View component.
 * Provides data access operations for participating driver information.
 */
@Repository
interface DriverInfoRepository : JpaRepository<DriverInfoEntity, Long> {
    fun findByRaceId(raceId: String): List<DriverInfoEntity>
    fun findByRaceIdAndDriverId(raceId: String, driverId: String): DriverInfoEntity?
}

