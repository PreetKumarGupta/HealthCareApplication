package health.com.HealthCareApplication.controller;

import health.com.HealthCareApplication.dto.AppointmentRequest;
import health.com.HealthCareApplication.entity.Appointment;
import health.com.HealthCareApplication.service.AppointmentService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/appointments")

public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(AppointmentService appointmentService){
        this.appointmentService=appointmentService;
    }

    /**
    *Book an appointment
    *Post /api/appointments/book
    *body: {"doctorId":1, "patientId":2, "appointmentDateTime":"2025-11-15T10:30:00"}
    * */

@PostMapping("/book")
    public ResponseEntity<?>book(@RequestBody AppointmentRequest req){
    try{
        Appointment appt = appointmentService.bookAppointment(req.getDoctorId(), req.getPatientId(), req.getAppointmentDateTime());
         return ResponseEntity.ok(appt);
    } catch(Exception ex){
        return  ResponseEntity.badRequest().body(ex.getMessage());
    }
}
/**
 * Cancel an appointment
 * POST /api/appointments/{id}/cancel
 */

@PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id){
    try{
        Appointment appt = appointmentService.cancelAppointment(id);
        return ResponseEntity.ok(appt);
    }catch(Exception ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}

/**
 * Reschedule an appointment
 * POST /api/appointments/{id}/reschedule
 * body: { "appointmentDateTime":"2025-11-21T11:00:00" }
 */

@PostMapping("/{id}/reschedule")
    public ResponseEntity<?> rescheduleAppointment(@PathVariable Long id,@RequestBody AppointmentRequest req){
    try{
        Appointment appt = appointmentService.rescheduleAppointment(id,req.getAppointmentDateTime());
        return ResponseEntity.ok(appt);
    }catch(Exception ex){
        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
/**
 * Get appointments for a patient
 * GET /api/appointments/patient/{patientId}
 */
@GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Appointment>> getAppointmentsForPatient(@PathVariable Long patientId){
    return ResponseEntity.ok(appointmentService.getAppointmentsForPatient(patientId));
    }

    /**
     * Get appointments for a doctor in a range
     * GET /api/appointments/doctor/{doctorId}?start=2025-11-10T00:00:00&end=2025-11-20T23:59:59
     */

    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Appointment>> getAppointmentsForDoctorInRange(
            @PathVariable Long doctorId,
            @RequestParam("start")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime start,
            @RequestParam("end")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)LocalDateTime end){
        return ResponseEntity.ok(appointmentService.getAppointmentsForDoctorInRange(doctorId,start,end));
    }
    /**
     * Check doctor availability for a specific time
     * GET /api/appointments/doctor/{doctorId}/available?time=2025-11-20T10:30:00
     */

    @GetMapping("/doctor/{doctorId}/available")
    public ResponseEntity<Boolean> isDoctorAvailable(
        @PathVariable Long doctorId,
        @RequestParam("time")@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime time){
        return ResponseEntity.ok(appointmentService.isDoctorAvailable(doctorId,time));
    }
}
