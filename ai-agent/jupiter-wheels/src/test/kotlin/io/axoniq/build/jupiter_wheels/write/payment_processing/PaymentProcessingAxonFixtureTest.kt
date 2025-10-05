package io.axoniq.build.jupiter_wheels.write.payment_processing

import io.axoniq.build.jupiter_wheels.payment_processing.*
import io.axoniq.build.jupiter_wheels.payment_processing.api.*
import org.axonframework.test.fixture.AxonTestFixture
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.assertj.core.api.Assertions.assertThat
import java.util.UUID

/**
 * Payment Processing Axon Fixture Test - Tests the Payment Processing component.
 * Verifies command handling, event sourcing, and business logic using Axon Framework test fixtures.
 */
class PaymentProcessingAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, PaymentProcessingState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("PaymentProcessing")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> PaymentProcessingCommandHandler() }
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
    fun `given bike rental requested, when return payment details, then payment prepared`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .`when`()
            .command(ReturnPaymentDetails(paymentId, rentalId))
            .then()
            .success()
            .events(PaymentPrepared(paymentId, rentalId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentDetailsResult
                assertThat(payload.paymentId).isEqualTo(paymentId)
                assertThat(payload.redirectUrl).contains(paymentId)
            }
    }

    @Test
    fun `given payment prepared, when confirm payment success, then payment completed`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .`when`()
            .command(ConfirmPaymentSuccess(paymentId, rentalId))
            .then()
            .success()
            .events(PaymentCompleted(paymentId, rentalId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentSuccessResult
                assertThat(payload.paymentStatus).isEqualTo("COMPLETED")
            }
    }

    @Test
    fun `given payment prepared, when report payment failure, then payment failed`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()
        val failureReason = "Insufficient funds"

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .`when`()
            .command(ReportPaymentFailure(failureReason, paymentId, rentalId))
            .then()
            .success()
            .events(PaymentFailed(failureReason, paymentId, rentalId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentFailureResult
                assertThat(payload.retryAllowed).isTrue()
            }
    }

    @Test
    fun `given payment prepared, when report permanent failure, then retry not allowed`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()
        val failureReason = "PERMANENT card decline"

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .`when`()
            .command(ReportPaymentFailure(failureReason, paymentId, rentalId))
            .then()
            .success()
            .events(PaymentFailed(failureReason, paymentId, rentalId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentFailureResult
                assertThat(payload.retryAllowed).isFalse()
            }
    }

    @Test
    fun `given payment prepared, when confirm payment cancellation, then payment cancelled`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .`when`()
            .command(ConfirmPaymentCancellation(paymentId, rentalId))
            .then()
            .success()
            .events(PaymentCancelled(paymentId, rentalId))
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentCancellationResult
                assertThat(payload.cancellationConfirmed).isTrue()
            }
    }

    @Test
    fun `given payment failed, when retry payment with valid method, then payment completed`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .event(PaymentFailed("Network error", paymentId, rentalId))
            .`when`()
            .command(RetryPayment("credit_card", rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PaymentCompleted
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.paymentId).isNotNull()
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentRetryResult
                assertThat(payload.paymentId).isNotNull()
                assertThat(payload.redirectUrl).isNull()
            }
    }

    @Test
    fun `given payment failed, when retry payment with invalid method, then payment failed again`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .event(PaymentFailed("Network error", paymentId, rentalId))
            .`when`()
            .command(RetryPayment("INVALID_METHOD", rentalId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PaymentFailed
                assertThat(event.rentalId).isEqualTo(rentalId)
                assertThat(event.failureReason).contains("Invalid payment method")
            }
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PaymentRetryResult
                assertThat(payload.paymentId).isNull()
                assertThat(payload.redirectUrl).isNull()
            }
    }

    @Test
    fun `given no payment prepared, when confirm payment success, then exception`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .`when`()
            .command(ConfirmPaymentSuccess(paymentId, rentalId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Payment success can only be confirmed for prepared payments")
            }
    }

    @Test
    fun `given no payment prepared, when report payment failure, then exception`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .`when`()
            .command(ReportPaymentFailure("Card expired", paymentId, rentalId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Payment failure can only be reported for prepared payments")
            }
    }

    @Test
    fun `given payment completed, when retry payment, then exception`() {
        val rentalId = UUID.randomUUID().toString()
        val paymentId = UUID.randomUUID().toString()

        fixture.given()
            .event(BikeRentalRequested("user123", rentalId, "bike456"))
            .event(PaymentPrepared(paymentId, rentalId))
            .event(PaymentCompleted(paymentId, rentalId))
            .`when`()
            .command(RetryPayment("credit_card", rentalId))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalArgumentException::class.java)
                    .hasMessageContaining("Payment retry can only be attempted for failed payments")
            }
    }
}