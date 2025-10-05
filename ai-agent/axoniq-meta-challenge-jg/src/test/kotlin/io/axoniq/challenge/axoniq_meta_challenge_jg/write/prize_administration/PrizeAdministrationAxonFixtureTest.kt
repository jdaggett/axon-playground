package io.axoniq.challenge.axoniq_meta_challenge_jg.write.prize_administration

import io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.prize_administration.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class PrizeAdministrationAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, PrizeAdministrationState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("PrizeAdministration")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> PrizeAdministrationCommandHandler() }

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
    fun `given no prior activity, when select prize winners, then winners selected event published`() {
        val command = SelectPrizeWinners(
            employeeId = "emp123",
            selectedWinnerIds = listOf("winner1", "winner2", "winner3")
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as WinnerSelectionResult
                assertThat(payload.selectedCount).isEqualTo(3)
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as WinnersSelected
                assertThat(event.winnerIds).containsExactly("winner1", "winner2", "winner3")
                assertThat(event.selectionTime).isNotNull
            }
    }

    @Test
    fun `given no winners selected, when select empty winner list, then exception thrown`() {
        val command = SelectPrizeWinners(
            employeeId = "emp123",
            selectedWinnerIds = emptyList()
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("No winners selected")
            }
    }

    @Test
    fun `given winners selected, when announce prize winners, then prizes announced event published`() {
        val winnersSelectedEvent = WinnersSelected(
            selectionTime = LocalDateTime.now().minusHours(1),
            winnerIds = listOf("winner1", "winner2")
        )

        fixture.given()
            .event(winnersSelectedEvent)
            .`when`()
            .command(AnnounceSelectedPrizeWinners())
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PrizeAnnouncementResult
                assertThat(payload.isSuccessful).isTrue
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PrizesAnnounced
                assertThat(event.announcementTime).isNotNull
            }
    }

    @Test
    fun `given no winners selected, when announce prize winners, then exception thrown`() {
        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(AnnounceSelectedPrizeWinners())
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("No winners selected yet")
            }
    }

    @Test
    fun `given prizes already announced, when announce prize winners again, then exception thrown`() {
        val winnersSelectedEvent = WinnersSelected(
            selectionTime = LocalDateTime.now().minusHours(2),
            winnerIds = listOf("winner1", "winner2")
        )
        val prizesAnnouncedEvent = PrizesAnnounced(
            announcementTime = LocalDateTime.now().minusHours(1)
        )

        fixture.given()
            .event(winnersSelectedEvent)
            .event(prizesAnnouncedEvent)
            .`when`()
            .command(AnnounceSelectedPrizeWinners())
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("already been announced")
            }
    }

    @Test
    fun `given winners selected and announced, when claim prize by winner, then prize claimed event published`() {
        val winnersSelectedEvent = WinnersSelected(
            selectionTime = LocalDateTime.now().minusHours(2),
            winnerIds = listOf("winner1", "winner2")
        )
        val prizesAnnouncedEvent = PrizesAnnounced(
            announcementTime = LocalDateTime.now().minusHours(1)
        )

        val command = ClaimPrize(
            participantId = "winner1",
            prizeId = "prize123"
        )

        fixture.given()
            .event(winnersSelectedEvent)
            .event(prizesAnnouncedEvent)
            .`when`()
            .command(command)
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PrizeClaimResult
                assertThat(payload.isSuccessful).isTrue
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PrizeClaimed
                assertThat(event.participantId).isEqualTo("winner1")
                assertThat(event.prizeId).isEqualTo("prize123")
                assertThat(event.claimTime).isNotNull
            }
    }

    @Test
    fun `given participant is not a winner, when claim prize, then exception thrown`() {
        val winnersSelectedEvent = WinnersSelected(
            selectionTime = LocalDateTime.now().minusHours(2),
            winnerIds = listOf("winner1", "winner2")
        )
        val prizesAnnouncedEvent = PrizesAnnounced(
            announcementTime = LocalDateTime.now().minusHours(1)
        )

        val command = ClaimPrize(
            participantId = "notAWinner",
            prizeId = "prize123"
        )

        fixture.given()
            .event(winnersSelectedEvent)
            .event(prizesAnnouncedEvent)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("is not a selected winner")
            }
    }

    @Test
    fun `given prizes not announced yet, when claim prize, then exception thrown`() {
        val winnersSelectedEvent = WinnersSelected(
            selectionTime = LocalDateTime.now().minusHours(1),
            winnerIds = listOf("winner1", "winner2")
        )

        val command = ClaimPrize(
            participantId = "winner1",
            prizeId = "prize123"
        )

        fixture.given()
            .event(winnersSelectedEvent)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("have not been announced yet")
            }
    }

    @Test
    fun `given prize already claimed, when claim same prize, then exception thrown`() {
        val winnersSelectedEvent = WinnersSelected(
            selectionTime = LocalDateTime.now().minusHours(3),
            winnerIds = listOf("winner1", "winner2")
        )
        val prizesAnnouncedEvent = PrizesAnnounced(
            announcementTime = LocalDateTime.now().minusHours(2)
        )
        val prizeClaimedEvent = PrizeClaimed(
            participantId = "winner1",
            prizeId = "prize123",
            claimTime = LocalDateTime.now().minusHours(1)
        )

        val command = ClaimPrize(
            participantId = "winner2",
            prizeId = "prize123"
        )

        fixture.given()
            .event(winnersSelectedEvent)
            .event(prizesAnnouncedEvent)
            .event(prizeClaimedEvent)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("has already been claimed")
            }
    }
}