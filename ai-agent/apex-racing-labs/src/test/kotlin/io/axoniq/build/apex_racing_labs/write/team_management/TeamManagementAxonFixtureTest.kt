package io.axoniq.build.apex_racing_labs.write.team_management

import io.axoniq.build.apex_racing_labs.team_management.TeamManagementCommandHandler
import io.axoniq.build.apex_racing_labs.team_management.TeamManagementState
import io.axoniq.build.apex_racing_labs.team_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * Test class for Team Management Service component using Axon Framework test fixture.
 * Verifies command handling, event sourcing, and business logic scenarios.
 */
class TeamManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, TeamManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("TeamManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> TeamManagementCommandHandler() }

        configurer = configurer.registerEntity(stateEntity)
            .registerCommandHandlingModule(commandHandlingModule)
            .componentRegistry { cr -> cr.disableEnhancer(AxonServerConfigurationEnhancer::class.java) }

        fixture = AxonTestFixture.with(configurer)
    }

    @AfterEach
    fun afterEach() {
        fixture.stop()
    }

    @Test
    fun `given no prior activity, when create team, then team created successfully`() {
        val teamId = "team-001"
        val teamName = "Racing Demons"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(CreateTeam(teamId, teamName))
            .then()
            .success()
            .events(TeamCreated(teamId, teamName))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TeamCreationResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Team created successfully")
            }
    }

    @Test
    fun `given team already exists, when create team with same id, then creation fails`() {
        val teamId = "team-001"
        val teamName = "Racing Demons"

        fixture.given()
            .event(TeamCreated(teamId, teamName))
            .`when`()
            .command(CreateTeam(teamId, "New Team Name"))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TeamCreationResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).contains("already exists and is active")
            }
    }

    @Test
    fun `given team was removed, when create team with same id, then team created successfully`() {
        val teamId = "team-001"
        val teamName = "Racing Demons"

        fixture.given()
            .event(TeamCreated(teamId, "Old Team"))
            .event(TeamRemoved(teamId))
            .`when`()
            .command(CreateTeam(teamId, teamName))
            .then()
            .success()
            .events(TeamCreated(teamId, teamName))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TeamCreationResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Team created successfully")
            }
    }

    @Test
    fun `given team exists, when remove team, then team removed successfully`() {
        val teamId = "team-001"
        val teamName = "Racing Demons"

        fixture.given()
            .event(TeamCreated(teamId, teamName))
            .`when`()
            .command(RemoveTeam(teamId))
            .then()
            .success()
            .events(TeamRemoved(teamId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TeamRemovalResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Team removed successfully")
            }
    }

    @Test
    fun `given no team exists, when remove team, then removal fails`() {
        val teamId = "non-existent-team"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RemoveTeam(teamId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TeamRemovalResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).contains("does not exist or is already removed")
            }
    }

    @Test
    fun `given team was already removed, when remove team again, then removal fails`() {
        val teamId = "team-001"
        val teamName = "Racing Demons"

        fixture.given()
            .event(TeamCreated(teamId, teamName))
            .event(TeamRemoved(teamId))
            .`when`()
            .command(RemoveTeam(teamId))
            .then()
            .success()
            .noEvents()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as TeamRemovalResult
                assertThat(payload.success).isFalse()
                assertThat(payload.message).contains("does not exist or is already removed")
            }
    }

    @Test
    fun `given team created and removed multiple times, when checking state evolution, then state is consistent`() {
        val teamId = "team-001"
        val teamName = "Racing Demons"

        fixture.given()
            .event(TeamCreated(teamId, teamName))
            .event(TeamRemoved(teamId))
            .event(TeamCreated(teamId, "New Team Name"))
            .`when`()
            .command(RemoveTeam(teamId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as TeamRemoved
                assertThat(event.teamId).isEqualTo(teamId)
            }
    }
}