package io.axoniq.build.caretrack.write.family_access_management

import io.axoniq.build.caretrack.family_access_management.FamilyAccessManagementCommandHandler
import io.axoniq.build.caretrack.family_access_management.FamilyAccessManagementState
import io.axoniq.build.caretrack.family_access_management.api.*
import org.assertj.core.api.Assertions.assertThat
import org.axonframework.axonserver.connector.AxonServerConfigurationEnhancer
import org.axonframework.commandhandling.configuration.CommandHandlingModule
import org.axonframework.eventsourcing.configuration.EventSourcedEntityModule
import org.axonframework.eventsourcing.configuration.EventSourcingConfigurer
import org.axonframework.test.fixture.AxonTestFixture
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class FamilyAccessManagementAxonFixtureTest {

    private lateinit var fixture: AxonTestFixture

    @BeforeEach
    fun beforeEach() {
        var configurer = EventSourcingConfigurer.create()
        val stateEntity = EventSourcedEntityModule
            .annotated(String::class.java, FamilyAccessManagementState::class.java)

        val commandHandlingModule = CommandHandlingModule
            .named("FamilyAccessManagement")
            .commandHandlers()
            .annotatedCommandHandlingComponent { c -> FamilyAccessManagementCommandHandler() }

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
    fun `given no prior activity, when invite family member, then invitation sent`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val accessLevel = "READ"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(InviteFamilyMember(familyMemberEmail, accessLevel, patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FamilyInvitationResult
                assertThat(payload.invitationSent).isTrue()
                assertThat(payload.invitationId).isNotEmpty()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FamilyMemberInvitationSent
                assertThat(event.patientId).isEqualTo(patientId)
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
                assertThat(event.accessLevel).isEqualTo(accessLevel)
                assertThat(event.invitationId).isNotEmpty()
            }
    }

    @Test
    fun `given family member already has access, when invite family member, then invitation not sent`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val accessLevel = "READ"

        fixture.given()
            .event(FamilyMemberAccessGranted(familyMemberEmail, accessLevel, patientId))
            .`when`()
            .command(InviteFamilyMember(familyMemberEmail, "WRITE", patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FamilyInvitationResult
                assertThat(payload.invitationSent).isFalse()
                assertThat(payload.invitationId).isEmpty()
            }
            .noEvents()
    }

    @Test
    fun `given pending invitation, when accept invitation, then access granted`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val accessLevel = "READ"
        val invitationId = "invitation123"

        fixture.given()
            .event(FamilyMemberInvitationSent(patientId, familyMemberEmail, accessLevel, invitationId))
            .`when`()
            .command(AcceptFamilyInvitation(familyMemberEmail, invitationId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FamilyAcceptanceResult
                assertThat(payload.accessGranted).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FamilyMemberAccessGranted
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
                assertThat(event.accessLevel).isEqualTo(accessLevel)
                assertThat(event.patientId).isEqualTo(patientId)
            }
    }

    @Test
    fun `given no pending invitation, when accept invitation, then access not granted`() {
        val familyMemberEmail = "family@example.com"
        val invitationId = "nonexistent123"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(AcceptFamilyInvitation(familyMemberEmail, invitationId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FamilyAcceptanceResult
                assertThat(payload.accessGranted).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given pending invitation, when decline invitation, then invitation declined`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val accessLevel = "READ"
        val invitationId = "invitation123"

        fixture.given()
            .event(FamilyMemberInvitationSent(patientId, familyMemberEmail, accessLevel, invitationId))
            .`when`()
            .command(DeclineFamilyInvitation(familyMemberEmail, invitationId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as FamilyDeclineResult
                assertThat(payload.invitationDeclined).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FamilyMemberInvitationDeclined
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
                assertThat(event.invitationId).isEqualTo(invitationId)
            }
    }

    @Test
    fun `given family member has access, when change permissions, then permissions updated`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val oldAccessLevel = "READ"
        val newAccessLevel = "WRITE"

        fixture.given()
            .event(FamilyMemberAccessGranted(familyMemberEmail, oldAccessLevel, patientId))
            .`when`()
            .command(ChangeFamilyMemberPermissions(familyMemberEmail, newAccessLevel, patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PermissionUpdateResult
                assertThat(payload.permissionsUpdated).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FamilyMemberPermissionsChanged
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
                assertThat(event.newAccessLevel).isEqualTo(newAccessLevel)
                assertThat(event.patientId).isEqualTo(patientId)
            }
    }

    @Test
    fun `given family member has no access, when change permissions, then permissions not updated`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val newAccessLevel = "WRITE"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(ChangeFamilyMemberPermissions(familyMemberEmail, newAccessLevel, patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PermissionUpdateResult
                assertThat(payload.permissionsUpdated).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given family member has same access level, when change permissions, then permissions not updated`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val accessLevel = "READ"

        fixture.given()
            .event(FamilyMemberAccessGranted(familyMemberEmail, accessLevel, patientId))
            .`when`()
            .command(ChangeFamilyMemberPermissions(familyMemberEmail, accessLevel, patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as PermissionUpdateResult
                assertThat(payload.permissionsUpdated).isFalse()
            }
            .noEvents()
    }

    @Test
    fun `given family member has access, when remove access, then access removed`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"
        val accessLevel = "READ"

        fixture.given()
            .event(FamilyMemberAccessGranted(familyMemberEmail, accessLevel, patientId))
            .`when`()
            .command(RemoveFamilyMemberAccess(familyMemberEmail, patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AccessRemovalResult
                assertThat(payload.accessRemoved).isTrue()
            }
            .eventsSatisfy { events ->
                assertThat(events).hasSize(1)
                val event = events[0] as FamilyMemberAccessRevoked
                assertThat(event.familyMemberEmail).isEqualTo(familyMemberEmail)
                assertThat(event.patientId).isEqualTo(patientId)
            }
    }

    @Test
    fun `given family member has no access, when remove access, then access not removed`() {
        val patientId = "patient123"
        val familyMemberEmail = "family@example.com"

        fixture.given()
            .noPriorActivity()
            .`when`()
            .command(RemoveFamilyMemberAccess(familyMemberEmail, patientId))
            .then()
            .success()
            .resultMessageSatisfies { result ->
                val payload = result.payload() as AccessRemovalResult
                assertThat(payload.accessRemoved).isFalse()
            }
            .noEvents()
    }
}