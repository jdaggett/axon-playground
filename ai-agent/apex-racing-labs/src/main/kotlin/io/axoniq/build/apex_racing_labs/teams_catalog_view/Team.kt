package io.axoniq.build.apex_racing_labs.teams_catalog_view

import jakarta.persistence.*
import kotlin.Boolean
import kotlin.String

/**
 * JPA entity representing a team in the Teams Catalog View component.
 * This entity stores team information including id, name and active status.
 */
@Entity
@Table(name = "teams")
data class Team(
    @Id
    @Column(name = "team_id")
    val teamId: String,
    
    @Column(name = "active", nullable = false)
    val active: Boolean,

    @Column(name = "team_name", nullable = false)
    val teamName: String
) {
    constructor() : this("", false, "")
}

