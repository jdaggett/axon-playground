package io.axoniq.build.dance_test.write.payment_management

import io.axoniq.build.dance_test.payment_management.PaymentManagementCommandHandler
import io.axoniq.build.dance_test.payment_management.PaymentManagementState
import io.axoniq.build.dance_test.payment_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate

/**
 * Test class for the Payment Management component using AxonTestFixture.
 * Tests command handling, event sourcing, and state evolution.
 */
class PaymentManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, PaymentManagementState::class.java)
        val commandHandlingModule = CommandHandlingModule
            .named("PaymentManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> PaymentManagementCommandHandler() }
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
    fun `given no prior activity, when record student payment, then payment recorded and balance increased`() {
        val studentId = "student123"
        val amount = 100.0
        val paymentMethod = "Credit Card"
        val paymentDate = LocalDate.now()

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RecordStudentPayment(amount, paymentMethod, paymentDate, studentId))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(2)
                
                val paymentRecorded = events[0] as PaymentRecorded
                assertThat(paymentRecorded.amount).isEqualTo(amount)
                assertThat(paymentRecorded.paymentMethod).isEqualTo(paymentMethod)
                assertThat(paymentRecorded.paymentDate).isEqualTo(paymentDate)
                assertThat(paymentRecorded.studentId).isEqualTo(studentId)
                
                val balanceIncreased = events[1] as MonetaryBalanceIncreasedFromPayment
                assertThat(balanceIncreased.amount).isEqualTo(amount)
                assertThat(balanceIncreased.studentId).isEqualTo(studentId)
            }
            .resultMessageSatisfies { result ->
                val paymentResult = result.payload() as PaymentResult
                assertThat(paymentResult.success).isTrue()
                assertThat(paymentResult.newBalance).isEqualTo(amount)
            }
    }
    
    @Test
    fun `given existing monetary balance, when record payment, then balance increased correctly`() {
        val studentId = "student123"
        val existingAmount = 50.0
        val newPaymentAmount = 75.0
        val expectedTotal = existingAmount + newPaymentAmount

        fixture.given()
            .event(MonetaryBalanceIncreasedFromPayment(existingAmount, studentId))
            .`when`()
            .command(RecordStudentPayment(newPaymentAmount, "Cash", LocalDate.now(), studentId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val paymentResult = result.payload() as PaymentResult
                assertThat(paymentResult.success).isTrue()
                assertThat(paymentResult.newBalance).isEqualTo(expectedTotal)
            }
    }

    @Test
    fun `given no prior activity, when adjust student balance positive, then adjustment recorded`() {
        val studentId = "student456"
        val adjustmentAmount = 25.0
        val adjustmentReason = "Refund for cancelled class"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(AdjustStudentBalance(adjustmentAmount, studentId, adjustmentReason))
            .then()
            .success()
            .events(BalanceAdjustmentRecorded(adjustmentAmount, studentId, adjustmentReason))
            .resultMessageSatisfies { result ->
                val adjustmentResult = result.payload() as BalanceAdjustmentResult
                assertThat(adjustmentResult.success).isTrue()
                assertThat(adjustmentResult.newBalance).isEqualTo(adjustmentAmount)
            }
    }

    @Test
    fun `given existing balance, when adjust student balance negative, then balance decreased`() {
        val studentId = "student789"
        val existingBalance = 100.0
        val negativeAdjustment = -30.0
        val expectedBalance = existingBalance + negativeAdjustment

        fixture.given()
            .event(MonetaryBalanceIncreasedFromPayment(existingBalance, studentId))
            .`when`()
            .command(AdjustStudentBalance(negativeAdjustment, studentId, "Administrative fee"))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val adjustmentResult = result.payload() as BalanceAdjustmentResult
                assertThat(adjustmentResult.success).isTrue()
                assertThat(adjustmentResult.newBalance).isEqualTo(expectedBalance)
            }
    }

    @Test
    fun `given lesson balance events, when state evolves, then lesson balance updated correctly`() {
        val studentId = "student999"
        val initialLessons = 10
        val usedLessons = 3
        val packageId = "package123"
        val sessionId = "session456"

        fixture.given()
            .event(LessonBalanceIncreasedFromPackage(initialLessons, packageId, studentId))
            .event(LessonBalanceDecreasedFromSession(usedLessons, studentId, sessionId))
            .`when`()
            .command(AdjustStudentBalance(0.0, studentId, "Test adjustment"))
            .then()
            .success()
    }
    
    @Test
    fun `given multiple payment events, when state evolves, then monetary balance accumulated correctly`() {
        val studentId = "student111"
        val payment1 = 50.0
        val payment2 = 30.0
        val adjustment = 10.0
        val finalAdjustment = 5.0
        val expectedTotal = payment1 + payment2 + adjustment + finalAdjustment

        fixture.given()
            .event(MonetaryBalanceIncreasedFromPayment(payment1, studentId))
            .event(MonetaryBalanceIncreasedFromPayment(payment2, studentId))
            .event(BalanceAdjustmentRecorded(adjustment, studentId, "Bonus credit"))
            .`when`()
            .command(AdjustStudentBalance(finalAdjustment, studentId, "Final adjustment"))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val adjustmentResult = result.payload() as BalanceAdjustmentResult
                assertThat(adjustmentResult.success).isTrue()
                assertThat(adjustmentResult.newBalance).isEqualTo(expectedTotal)
            }
    }
}