package io.axoniq.build.apex_racing_labs.write.user_preferences

import io.axoniq.build.apex_racing_labs.user_preferences.UserPreferencesCommandHandler
import io.axoniq.build.apex_racing_labs.user_preferences.UserPreferencesState
import io.axoniq.build.apex_racing_labs.user_preferences.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

/**
 * UserPreferencesAxonFixtureTest - Test class for the User Preferences Service component.
 * Tests command handling and event sourcing for user preference management.
 */
class UserPreferencesAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, UserPreferencesState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("UserPreferences")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> UserPreferencesCommandHandler() }

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
    fun `given no prior activity, when select favorite team, then favorite team selected event is published`() {
        val userId = "user-123"
        val teamId = "team-456"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(SelectFavoriteTeam(teamId = teamId, userId = userId))
            .then()
            .success()
            .events(FavoriteTeamSelected(teamId = teamId, userId = userId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FavoriteTeamResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Favorite team selected successfully")
            }
    }

    @Test
    fun `given no prior activity, when select favorite driver, then favorite driver selected event is published`() {
        val userId = "user-123"
        val driverId = "driver-789"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(SelectFavoriteDriver(userId = userId, driverId = driverId))
            .then()
            .success()
            .events(FavoriteDriverSelected(userId = userId, driverId = driverId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FavoriteDriverResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Favorite driver selected successfully")
            }
    }

    @Test
    fun `given favorite team selected, when select new favorite team, then new team selected event is published`() {
        val userId = "user-123"
        val oldTeamId = "team-456"
        val newTeamId = "team-999"

        fixture.given()
            .event(FavoriteTeamSelected(teamId = oldTeamId, userId = userId))
            .`when`()
            .command(SelectFavoriteTeam(teamId = newTeamId, userId = userId))
            .then()
            .success()
            .events(FavoriteTeamSelected(teamId = newTeamId, userId = userId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FavoriteTeamResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Favorite team selected successfully")
            }
    }

    @Test
    fun `given favorite driver selected, when select new favorite driver, then new driver selected event is published`() {
        val userId = "user-123"
        val oldDriverId = "driver-789"
        val newDriverId = "driver-111"

        fixture.given()
            .event(FavoriteDriverSelected(userId = userId, driverId = oldDriverId))
            .`when`()
            .command(SelectFavoriteDriver(userId = userId, driverId = newDriverId))
            .then()
            .success()
            .events(FavoriteDriverSelected(userId = userId, driverId = newDriverId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FavoriteDriverResult
                assertThat(payload.success).isTrue()
                assertThat(payload.message).isEqualTo("Favorite driver selected successfully")
            }
    }
    
    @Test
    fun `given both team and driver selected, when select new team, then team updated and driver remains`() {
        val userId = "user-123"
        val oldTeamId = "team-456"
        val driverId = "driver-789"
        val newTeamId = "team-999"

        fixture.given()
            .event(FavoriteTeamSelected(teamId = oldTeamId, userId = userId))
            .event(FavoriteDriverSelected(userId = userId, driverId = driverId))
            .`when`()
            .command(SelectFavoriteTeam(teamId = newTeamId, userId = userId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FavoriteTeamSelected
                assertThat(event.userId).isEqualTo(userId)
                assertThat(event.teamId).isEqualTo(newTeamId)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FavoriteTeamResult
                assertThat(payload.success).isTrue()
            }
    }

    @Test
    fun `given both team and driver selected, when select new driver, then driver updated and team remains`() {
        val userId = "user-123"
        val teamId = "team-456"
        val oldDriverId = "driver-789"
        val newDriverId = "driver-111"

        fixture.given()
            .event(FavoriteTeamSelected(teamId = teamId, userId = userId))
            .event(FavoriteDriverSelected(userId = userId, driverId = oldDriverId))
            .`when`()
            .command(SelectFavoriteDriver(userId = userId, driverId = newDriverId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FavoriteDriverSelected
                assertThat(event.userId).isEqualTo(userId)
                assertThat(event.driverId).isEqualTo(newDriverId)
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FavoriteDriverResult
                assertThat(payload.success).isTrue()
            }
    }
}