package io.axoniq.build.caretrack.appointment_management

import io.axoniq.build.caretrack.appointment_management.api.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for the Appointment Management Service component.
 * Provides endpoints for patient appointment scheduling and tracking operations.
 */
@RestController
@RequestMapping("/api/appointments")
class AppointmentManagementServiceController(
    private val commandGateway: CommandGateway
) {
    companion object {
        private val logger: Logger = LoggerFactory.getLogger(AppointmentManagementServiceController::class.java)
    }

    /**
     * Endpoint to schedule a new patient appointment.
     */
    @PostMapping("/schedule")
    fun schedulePatientAppointment(@RequestBody command: SchedulePatientAppointment): ResponseEntity<String> {
        logger.info("Received SchedulePatientAppointment request: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Appointment scheduling request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to schedule patient appointment", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to schedule patient appointment")
        }
    }

    /**
     * Endpoint to mark an appointment as missed.
     */
    @PostMapping("/{appointmentId}/missed")
    fun markAppointmentMissed(
        @PathVariable appointmentId: String,
        @RequestBody request: MarkAppointmentMissedRequest
    ): ResponseEntity<String> {
        val command = MarkAppointmentMissed(
            doctorId = request.doctorId,
            appointmentId = appointmentId
        )
        logger.info("Received MarkAppointmentMissed request: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Mark appointment missed request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to mark appointment as missed", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to mark appointment as missed")
        }
    }

    /**
     * Endpoint to mark an appointment as attended.
     */
    @PostMapping("/{appointmentId}/attended")
    fun markAppointmentAttended(
        @PathVariable appointmentId: String,
        @RequestBody request: MarkAppointmentAttendedRequest
    ): ResponseEntity<String> {
        val command = MarkAppointmentAttended(
            doctorId = request.doctorId,
            appointmentId = appointmentId
        )
        logger.info("Received MarkAppointmentAttended request: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Mark appointment attended request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to mark appointment as attended", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to mark appointment as attended")
        }
    }

    /**
     * Endpoint to cancel a patient appointment.
     */
    @DeleteMapping("/{appointmentId}")
    fun cancelPatientAppointment(
        @PathVariable appointmentId: String,
        @RequestBody request: CancelPatientAppointmentRequest
    ): ResponseEntity<String> {
        val command = CancelPatientAppointment(
            doctorId = request.doctorId,
            cancellationReason = request.cancellationReason,
            appointmentId = appointmentId
        )
        logger.info("Received CancelPatientAppointment request: $command")
        return try {
            commandGateway.sendAndWait(command)
            ResponseEntity.status(HttpStatus.ACCEPTED).body("Cancel appointment request accepted")
        } catch (ex: Exception) {
            logger.error("Failed to cancel patient appointment", ex)
            ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to cancel patient appointment")
        }
    }

    // Request DTOs for endpoints
    data class MarkAppointmentMissedRequest(val doctorId: String)
    data class MarkAppointmentAttendedRequest(val doctorId: String)
    data class CancelPatientAppointmentRequest(val doctorId: String, val cancellationReason: String?)
}

