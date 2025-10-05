package io.axoniq.build.pet_clinic.write.pet_management

import io.axoniq.build.pet_clinic.pet_management.*
import io.axoniq.build.pet_clinic.pet_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

/**
 * Axon test fixture tests for Pet Management component.
 * Verifies command handling, event sourcing, and exception scenarios.
 */
class PetManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture
    
    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, PetManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("PetManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> RegisterPetCommandHandler() }

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
    fun `given no prior activity, when register pet, then pet registered event published`() {
        val petId = "pet-123"
        val email = "owner@example.com"
        val name = "Buddy"
        val birthday = Date()
        val type = "Dog"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RegisterPet(petId, email, name, birthday, type))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PetRegistered
                assertThat(event.petId).isEqualTo(petId)
                assertThat(event.email).isEqualTo(email)
                assertThat(event.name).isEqualTo(name)
                assertThat(event.birthday).isEqualTo(birthday)
                assertThat(event.type).isEqualTo(type)
            }
    }

    @Test
    fun `given pet already registered, when register pet again, then exception thrown`() {
        val petId = "pet-123"
        val email = "owner@example.com"
        val name = "Buddy"
        val birthday = Date()
        val type = "Dog"

        fixture.given()
            .event(PetRegistered(petId, email, name, birthday, type))
            .`when`()
            .command(RegisterPet(petId, email, "Updated Name", birthday, type))
            .then()
            .exceptionSatisfies { ex ->
                assertThat(ex)
                    .isInstanceOf(IllegalStateException::class.java)
                    .hasMessageContaining("Pet is already registered")
            }
    }

    @Test
    fun `given pet registered, when register different pet, then second pet registered`() {
        val firstPetId = "pet-123"
        val secondPetId = "pet-456"
        val email = "owner@example.com"
        val firstName = "Buddy"
        val secondName = "Max"
        val birthday = Date()
        val type = "Dog"

        fixture.given()
            .event(PetRegistered(firstPetId, email, firstName, birthday, type))
            .`when`()
            .command(RegisterPet(secondPetId, email, secondName, birthday, type))
            .then()
            .success()
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as PetRegistered
                assertThat(event.petId).isEqualTo(secondPetId)
                assertThat(event.email).isEqualTo(email)
                assertThat(event.name).isEqualTo(secondName)
                assertThat(event.birthday).isEqualTo(birthday)
                assertThat(event.type).isEqualTo(type)
            }
    }
}