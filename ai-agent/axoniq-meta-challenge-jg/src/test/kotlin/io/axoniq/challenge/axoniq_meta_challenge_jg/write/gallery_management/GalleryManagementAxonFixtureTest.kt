package io.axoniq.challenge.axoniq_meta_challenge_jg.write.gallery_management

import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.api.*
import io.axoniq.challenge.axoniq_meta_challenge_jg.gallery_management.exception.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

/**
 * Gallery Management Axon Fixture Tests
 * 
 * Tests the Gallery Management component using Axon Framework's test fixture
 * to verify command handling, event sourcing, and exception scenarios.
 */
class GalleryManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, GalleryManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("GalleryManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> GalleryManagementCommandHandler() }

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
    fun `given no prior activity, when share project to gallery, then project shared successfully`() {
        val participantId = "participant-123"
        val command = ShareProjectToGallery(
            applicationId = "app-001",
            participantId = participantId,
            projectTitle = "My Awesome Project"
        )
        
        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ProjectSharedToGallery
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.projectTitle).isEqualTo("My Awesome Project")
                assertThat(event.projectId).isNotNull()
                assertThat(event.submissionTime).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val sharingResult = result.payload() as ProjectSharingResult
                assertThat(sharingResult.isSuccessful).isTrue()
                assertThat(sharingResult.projectId).isNotNull()
            }
    }

    @Test
    fun `given project already shared, when share another project, then gallery sharing failed exception`() {
        val participantId = "participant-123"
        val existingEvent = ProjectSharedToGallery(
            submissionTime = LocalDateTime.now(),
            participantId = participantId,
            projectTitle = "First Project",
            projectId = "project-001"
        )

        val command = ShareProjectToGallery(
            applicationId = "app-001",
            participantId = participantId,
            projectTitle = "Second Project"
        )
        
        fixture.given()
            .event(existingEvent)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(GallerySharingFailed::class.java)
                    .hasMessageContaining("Participant has already shared a project to the gallery")
            }
    }

    @Test
    fun `given no prior activity, when vote for project, then vote registered successfully`() {
        val participantId = "participant-123"
        val projectId = "project-456"
        val command = VoteForProject(
            participantId = participantId,
            voteType = "LIKE",
            projectId = projectId
        )

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as VoteRegistered
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.voteType).isEqualTo("LIKE")
                assertThat(event.projectId).isEqualTo(projectId)
            }
            .resultMessageSatisfies { result ->
                val votingResult = result.payload() as VotingResult
                assertThat(votingResult.isSuccessful).isTrue()
            }
    }

    @Test
    fun `given already voted for project, when vote again for same project, then voting system error exception`() {
        val participantId = "participant-123"
        val projectId = "project-456"
        val existingVote = VoteRegistered(
            participantId = participantId,
            voteType = "LIKE",
            projectId = projectId
        )
        
        val command = VoteForProject(
            participantId = participantId,
            voteType = "DISLIKE",
            projectId = projectId
        )

        fixture.given()
            .event(existingVote)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(VotingSystemError::class.java)
                    .hasMessageContaining("Participant has already voted for this project")
            }
    }
    
    @Test
    fun `given voted for different project, when vote for new project, then vote registered successfully`() {
        val participantId = "participant-123"
        val existingProjectId = "project-111"
        val newProjectId = "project-222"

        val existingVote = VoteRegistered(
            participantId = participantId,
            voteType = "LIKE",
            projectId = existingProjectId
        )

        val command = VoteForProject(
            participantId = participantId,
            voteType = "DISLIKE",
            projectId = newProjectId
        )
        
        fixture.given()
            .event(existingVote)
            .`when`()
            .command(command)
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as VoteRegistered
                assertThat(event.participantId).isEqualTo(participantId)
                assertThat(event.voteType).isEqualTo("DISLIKE")
                assertThat(event.projectId).isEqualTo(newProjectId)
            }
    }

    @Test
    fun `given project shared and vote cast, when checking state evolution, then both events reflected in state`() {
        val participantId = "participant-123"
        val projectId = "project-456"
        val sharedProject = ProjectSharedToGallery(
            submissionTime = LocalDateTime.now(),
            participantId = participantId,
            projectTitle = "Shared Project",
            projectId = projectId
        )

        val vote = VoteRegistered(
            participantId = participantId,
            voteType = "LIKE",
            projectId = "different-project-789"
        )

        val command = ShareProjectToGallery(
            applicationId = "app-002",
            participantId = participantId,
            projectTitle = "Another Project"
        )
        
        fixture.given()
            .event(sharedProject)
            .event(vote)
            .`when`()
            .command(command)
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(GallerySharingFailed::class.java)
                    .hasMessageContaining("Participant has already shared a project to the gallery")
            }
    }
}