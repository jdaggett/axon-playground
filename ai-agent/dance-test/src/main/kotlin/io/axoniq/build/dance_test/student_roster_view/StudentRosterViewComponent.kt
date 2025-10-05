package io.axoniq.build.dance_test.student_roster_view

import io.axoniq.build.dance_test.student_roster_view.api.*
import org.axonframework.eventhandling.annotations.EventHandler
import org.axonframework.queryhandling.annotations.QueryHandler
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Student Roster View component - Provides student roster and detailed student information views.
 * This component handles queries for student details, roster data, and detailed student information.
 * It maintains a read model by processing relevant student and session events.
 */
@Component
class StudentRosterViewComponent(
    private val studentRosterRepository: StudentRosterRepository
) {

    companion object {
        private val logger: Logger = LoggerFactory.getLogger(StudentRosterViewComponent::class.java)
    }

    /**
     * Query handler for StudentDetails - retrieves basic student details by student ID.
     */
    @QueryHandler
    fun handle(query: StudentDetails): StudentDetailsData {
        logger.info("Handling StudentDetails query for studentId: ${query.studentId}")
        
        val entity = studentRosterRepository.findById(query.studentId)
            .orElseThrow { IllegalArgumentException("Student not found: ${query.studentId}") }

        return StudentDetailsData(
            monetaryBalance = entity.monetaryBalance,
            relationshipStatus = entity.relationshipStatus,
            studentId = entity.studentId,
            name = entity.name,
            lessonBalance = entity.lessonBalance,
            bookingAccessStatus = entity.bookingAccessStatus,
            phone = entity.phone
        )
    }
    
    /**
     * Query handler for StudentRoster - retrieves all students for a specific instructor.
     */
    @QueryHandler
    fun handle(query: StudentRoster): StudentRosterData {
        logger.info("Handling StudentRoster query for instructorId: ${query.instructorId}")
        
        val entities = studentRosterRepository.findByInstructorId(query.instructorId)
        
        val students = entities.map { entity ->
            StudentSummary(
                monetaryBalance = entity.monetaryBalance,
                relationshipStatus = entity.relationshipStatus,
                studentId = entity.studentId,
                name = entity.name,
                lessonBalance = entity.lessonBalance,
                bookingAccessStatus = entity.bookingAccessStatus,
                phone = entity.phone,
                lastBookingDate = entity.lastBookingDate
            )
        }
        
        return StudentRosterData(students = students)
    }
    
    /**
     * Query handler for DetailedStudentInformation - retrieves detailed information for a specific student.
     */
    @QueryHandler
    fun handle(query: DetailedStudentInformation): DetailedStudentData {
        logger.info("Handling DetailedStudentInformation query for instructorId: ${query.instructorId}, studentId: ${query.studentId}")

        val entity = studentRosterRepository.findByInstructorIdAndStudentId(query.instructorId, query.studentId)
            ?: throw IllegalArgumentException("Student not found for instructor ${query.instructorId} and student ${query.studentId}")
        
        return DetailedStudentData(
            monetaryBalance = entity.monetaryBalance,
            relationshipStatus = entity.relationshipStatus,
            totalSessionsCompleted = entity.totalSessionsCompleted,
            studentId = entity.studentId,
            name = entity.name,
            lessonBalance = entity.lessonBalance,
            bookingAccessStatus = entity.bookingAccessStatus,
            totalLifetimePayments = entity.totalLifetimePayments,
            phone = entity.phone,
            lastBookingDate = entity.lastBookingDate
        )
    }

    /**
     * Event handler for StudentProfileCreated - creates a new student entry in the view.
     */
    @EventHandler
    fun on(event: StudentProfileCreated) {
        logger.info("Handling StudentProfileCreated event for studentId: ${event.studentId}")
        
        val entity = StudentRosterEntity(
            studentId = event.studentId,
            instructorId = event.instructorId,
            name = event.name,
            phone = event.phone,
            monetaryBalance = 0.0,
            lessonBalance = 0,
            relationshipStatus = "Active", // Default status
            bookingAccessStatus = "Allowed", // Default access
            lastBookingDate = null,
            totalSessionsCompleted = 0,
            totalLifetimePayments = 0.0
        )

        studentRosterRepository.save(entity)
    }

    /**
     * Event handler for SessionScheduled - updates last booking date when a session is scheduled.
     */
    @EventHandler
    fun on(event: SessionScheduled) {
        logger.info("Handling SessionScheduled event for studentId: ${event.studentId}")
        
        val entity = studentRosterRepository.findById(event.studentId)
        if (entity.isPresent) {
            val updatedEntity = entity.get().copy(
                lastBookingDate = event.sessionDate.toLocalDate(),
                totalSessionsCompleted = entity.get().totalSessionsCompleted + 1
            )
            studentRosterRepository.save(updatedEntity)
        }
    }

    /**
     * Event handler for LessonBalanceIncreasedFromPackage - increases lesson balance.
     */
    @EventHandler
    fun on(event: LessonBalanceIncreasedFromPackage) {
        logger.info("Handling LessonBalanceIncreasedFromPackage event for studentId: ${event.studentId}")

        val entity = studentRosterRepository.findById(event.studentId)
        if (entity.isPresent) {
            val updatedEntity = entity.get().copy(
                lessonBalance = entity.get().lessonBalance + event.lessonCount
            )
            studentRosterRepository.save(updatedEntity)
        }
    }

    /**
     * Event handler for PaymentRecorded - updates total lifetime payments.
     */
    @EventHandler
    fun on(event: PaymentRecorded) {
        logger.info("Handling PaymentRecorded event for studentId: ${event.studentId}")

        val entity = studentRosterRepository.findById(event.studentId)
        if (entity.isPresent) {
            val updatedEntity = entity.get().copy(
                totalLifetimePayments = entity.get().totalLifetimePayments + event.amount
            )
            studentRosterRepository.save(updatedEntity)
        }
    }

    /**
     * Event handler for MonetaryBalanceIncreasedFromPayment - increases monetary balance.
     */
    @EventHandler
    fun on(event: MonetaryBalanceIncreasedFromPayment) {
        logger.info("Handling MonetaryBalanceIncreasedFromPayment event for studentId: ${event.studentId}")

        val entity = studentRosterRepository.findById(event.studentId)
        if (entity.isPresent) {
            val updatedEntity = entity.get().copy(
                monetaryBalance = entity.get().monetaryBalance + event.amount
            )
            studentRosterRepository.save(updatedEntity)
        }
    }
    
    /**
     * Event handler for BookingAccessStatusUpdated - updates booking access status.
     */
    @EventHandler
    fun on(event: BookingAccessStatusUpdated) {
        logger.info("Handling BookingAccessStatusUpdated event for studentId: ${event.studentId}")

        val entity = studentRosterRepository.findById(event.studentId)
        if (entity.isPresent) {
            val updatedEntity = entity.get().copy(
                bookingAccessStatus = event.newAccessStatus
            )
            studentRosterRepository.save(updatedEntity)
        }
    }
    
    /**
     * Event handler for LessonBalanceDecreasedFromSession - decreases lesson balance.
     */
    @EventHandler
    fun on(event: LessonBalanceDecreasedFromSession) {
        logger.info("Handling LessonBalanceDecreasedFromSession event for studentId: ${event.studentId}")

        val entity = studentRosterRepository.findById(event.studentId)
        if (entity.isPresent) {
            val updatedEntity = entity.get().copy(
                lessonBalance = entity.get().lessonBalance - event.lessonsUsed
            )
            studentRosterRepository.save(updatedEntity)
        }
    }

    /**
     * Event handler for StudentProfileDeleted - removes student from the view.
     */
    @EventHandler
    fun on(event: StudentProfileDeleted) {
        logger.info("Handling StudentProfileDeleted event for studentId: ${event.studentId}")
        
        studentRosterRepository.deleteById(event.studentId)
    }
}

