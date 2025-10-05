package io.axoniq.build.jupiter_wheels.write.bike_return_management

import io.axoniq.build.jupiter_wheels.bike_return_management.BikeReturnManagementCommandHandler
import io.axoniq.build.jupiter_wheels.bike_return_management.BikeReturnState
import io.axoniq.build.jupiter_wheels.bike_return_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class BikeReturnManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, BikeReturnState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("BikeReturnManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> BikeReturnManagementCommandHandler() }
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
    fun `given bike returned, when submit return survey, then survey submitted`() {
        val rentalId = "rental-123"

        fixture.given()
            .event(BikeReturned(returnLocation = "Central Station", rentalId = rentalId, bikeId = "bike-456"))
            .`when`()
            .command(SubmitReturnSurvey(feedback = "Great bike!", rating = 5, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ReturnSurveyResult
                assertThat(payload.surveyAccepted).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ReturnSurveySubmitted
                assertThat(event.feedback).isEqualTo("Great bike!")
                assertThat(event.rating).isEqualTo(5)
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }

    @Test
    fun `given bike marked as in use, when return bike at location, then bike returned`() {
        val rentalId = "rental-456"
        val returnLocation = "Park Station"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ReturnBikeAtLocation(returnLocation = returnLocation, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as BikeReturnResult
                assertThat(payload.returnConfirmed).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BikeReturned
                assertThat(event.returnLocation).isEqualTo(returnLocation)
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.bikeId).isEqualTo("")
            }
    }

    @Test
    fun `given no prior activity, when submit bike photo, then photo submitted and flagged`() {
        val rentalId = "rental-789"
        val photoUrl = "https://example.com/photo.jpg"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(SubmitBikePhoto(photoUrl = photoUrl, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as BikePhotoResult
                assertThat(payload.photoAccepted).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val photoSubmittedEvent = events[0] as BikePhotoSubmitted
                assertThat(photoSubmittedEvent.photoUrl).isEqualTo(photoUrl)
                assertThat(photoSubmittedEvent.rentalId).isEqualTo(rentalId)
                
                val flaggedEvent = events[1] as PhotoFlaggedForReview
                assertThat(flaggedEvent.photoUrl).isEqualTo(photoUrl)
                assertThat(flaggedEvent.rentalId).isEqualTo(rentalId)
            }
    }

    @Test
    fun `given photo submitted, when approve photo, then inspection completed`() {
        val rentalId = "rental-101"
        val bikeId = "bike-202"

        fixture.given()
            .event(BikeReturned(returnLocation = "Central Park", rentalId = rentalId, bikeId = bikeId))
            .event(BikePhotoSubmitted(photoUrl = "https://example.com/photo.jpg", rentalId = rentalId))
            .`when`()
            .command(ApproveOrRejectPhoto(approved = true, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PhotoApprovalResult
                assertThat(payload.approvalProcessed).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as BikeInspectionCompleted
                assertThat(event.inspectionPassed).isTrue()
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.bikeId).isEqualTo(bikeId)
            }
    }

    @Test
    fun `given photo submitted, when reject photo, then no inspection completed`() {
        val rentalId = "rental-303"

        fixture.given()
            .event(BikePhotoSubmitted(photoUrl = "https://example.com/bad-photo.jpg", rentalId = rentalId))
            .`when`()
            .command(ApproveOrRejectPhoto(approved = false, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PhotoApprovalResult
                assertThat(payload.approvalProcessed).isTrue()
            }
            .noEvents()
    }

    @Test
    fun `given bike returned, when report inspection passed, then inspection completed and bike available`() {
        val rentalId = "rental-404"
        val bikeId = "bike-505"

        fixture.given()
            .event(BikeReturned(returnLocation = "Downtown", rentalId = rentalId, bikeId = bikeId))
            .`when`()
            .command(ReportInspectionResults(inspectionPassed = true, issues = null, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as InspectionResult
                assertThat(payload.inspectionProcessed).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                val inspectionEvent = events[0] as BikeInspectionCompleted
                assertThat(inspectionEvent.inspectionPassed).isTrue()
                assertThat(inspectionEvent.rentalId).isEqualTo(rentalId)
                assertThat(inspectionEvent.bikeId).isEqualTo(bikeId)
                
                val availableEvent = events[1] as BikeMarkedAsAvailable
                assertThat(availableEvent.bikeId).isEqualTo(bikeId)
            }
    }

    @Test
    fun `given bike returned, when report inspection failed, then only inspection completed`() {
        val rentalId = "rental-606"
        val bikeId = "bike-707"

        fixture.given()
            .event(BikeReturned(returnLocation = "Uptown", rentalId = rentalId, bikeId = bikeId))
            .`when`()
            .command(ReportInspectionResults(inspectionPassed = false, issues = "Flat tire", rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as InspectionResult
                assertThat(payload.inspectionProcessed).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val inspectionEvent = events[0] as BikeInspectionCompleted
                assertThat(inspectionEvent.inspectionPassed).isFalse()
                assertThat(inspectionEvent.rentalId).isEqualTo(rentalId)
                assertThat(inspectionEvent.bikeId).isEqualTo(bikeId)
            }
    }

    @Test
    fun `given no prior activity, when submit return survey with minimal data, then survey submitted`() {
        val rentalId = "rental-808"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(SubmitReturnSurvey(feedback = null, rating = 3, rentalId = rentalId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as ReturnSurveyResult
                assertThat(payload.surveyAccepted).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as ReturnSurveySubmitted
                assertThat(event.feedback).isNull()
                assertThat(event.rating).isEqualTo(3)
                assertThat(event.rentalId).isEqualTo(rentalId)
            }
    }
}