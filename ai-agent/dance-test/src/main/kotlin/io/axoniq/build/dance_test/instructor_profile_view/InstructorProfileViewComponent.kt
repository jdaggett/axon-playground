package io.axoniq.build.dance_test.instructor_profile_view

import io.axoniq.build.dance_test.instructor_profile_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Instructor Profile View component that provides instructor profile settings and configuration views.
 * This component handles events and queries related to instructor profiles and lesson packages.
 */
@Component
class InstructorProfileViewComponent(
    private val instructorProfileRepository: InstructorProfileRepository,
    private val lessonPackageRepository: LessonPackageRepository
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(InstructorProfileViewComponent::class.java)
    }

    /**
     * Query handler for retrieving instructor profile settings.
     * Handles InstructorProfileSettings query and returns InstructorProfileData.
     */
    @QueryHandler
    fun handle(query: InstructorProfileSettings): InstructorProfileData? {
        logger.info("Handling InstructorProfileSettings query for instructorId: ${query.instructorId}")
        
        val profile = instructorProfileRepository.findById(query.instructorId).orElse(null)
        
        return profile?.let {
            InstructorProfileData(
                instructorId = it.instructorId,
                calendlyAccountId = it.calendlyAccountId,
                email = it.email,
                specialties = it.specialties,
                calendlyIntegrationStatus = it.calendlyIntegrationStatus,
                phone = it.phone
            )
        }
    }

    /**
     * Query handler for retrieving package details.
     * Handles PackageDetails query and returns PackageDetailsData.
     */
    @QueryHandler
    fun handle(query: PackageDetails): PackageDetailsData? {
        logger.info("Handling PackageDetails query for packageId: ${query.packageId}")

        val packageEntity = lessonPackageRepository.findById(query.packageId).orElse(null)
        
        return packageEntity?.let {
            PackageDetailsData(
                lessonCount = it.lessonCount,
                lessonDuration = it.lessonDuration,
                isActive = it.isActive,
                studentId = it.studentId,
                packageId = it.packageId,
                creationDate = it.creationDate,
                price = it.price
            )
        }
    }

    /**
     * Event handler for instructor profile creation events.
     * Creates a new instructor profile entity when InstructorProfileCreated event is received.
     */
    @EventHandler
    fun on(event: InstructorProfileCreated) {
        logger.info("Handling InstructorProfileCreated event for instructorId: ${event.instructorId}")

        val profile = InstructorProfileEntity(
            instructorId = event.instructorId,
            calendlyAccountId = null,
            email = event.email,
            specialties = event.specialties.toMutableList(),
            calendlyIntegrationStatus = "NOT_CONNECTED",
            phone = event.phone
        )

        instructorProfileRepository.save(profile)
        logger.info("Created instructor profile for instructorId: ${event.instructorId}")
    }

    /**
     * Event handler for Calendly integration connection events.
     * Updates instructor profile when CalendlyIntegrationConnected event is received.
     */
    @EventHandler
    fun on(event: CalendlyIntegrationConnected) {
        logger.info("Handling CalendlyIntegrationConnected event for instructorId: ${event.instructorId}")

        val profile = instructorProfileRepository.findById(event.instructorId).orElse(null)
        profile?.let {
            val updatedProfile = it.copy(
                calendlyAccountId = event.calendlyAccountId,
                calendlyIntegrationStatus = "CONNECTED"
            )
            instructorProfileRepository.save(updatedProfile)
            logger.info("Updated Calendly integration for instructorId: ${event.instructorId}")
        }
    }

    /**
     * Event handler for Calendly settings update events.
     * Updates instructor profile Calendly settings when CalendlySettingsUpdated event is received.
     */
    @EventHandler
    fun on(event: CalendlySettingsUpdated) {
        logger.info("Handling CalendlySettingsUpdated event for instructorId: ${event.instructorId}")

        val profile = instructorProfileRepository.findById(event.instructorId).orElse(null)
        profile?.let {
            val updatedProfile = it.copy(
                calendlyAccountId = event.calendlyAccountId
            )
            instructorProfileRepository.save(updatedProfile)
            logger.info("Updated Calendly settings for instructorId: ${event.instructorId}")
        }
    }

    /**
     * Event handler for custom lesson package creation events.
     * Creates a new lesson package entity when CustomLessonPackageCreated event is received.
     */
    @EventHandler
    fun on(event: CustomLessonPackageCreated) {
        logger.info("Handling CustomLessonPackageCreated event for packageId: ${event.packageId}")

        val packageEntity = LessonPackageEntity(
            packageId = event.packageId,
            lessonCount = event.lessonCount,
            lessonDuration = event.lessonDuration,
            isActive = true,
            studentId = event.studentId,
            creationDate = LocalDate.now(),
            price = event.price,
            instructorId = event.instructorId
        )
        
        lessonPackageRepository.save(packageEntity)
        logger.info("Created lesson package for packageId: ${event.packageId}")
    }

    /**
     * Event handler for lesson package deletion events.
     * Marks a lesson package as inactive when LessonPackageDeleted event is received.
     */
    @EventHandler
    fun on(event: LessonPackageDeleted) {
        logger.info("Handling LessonPackageDeleted event for packageId: ${event.packageId}")

        val packageEntity = lessonPackageRepository.findById(event.packageId).orElse(null)
        packageEntity?.let {
            val updatedPackage = it.copy(isActive = false)
            lessonPackageRepository.save(updatedPackage)
            logger.info("Marked lesson package as inactive for packageId: ${event.packageId}")
        }
    }
}

