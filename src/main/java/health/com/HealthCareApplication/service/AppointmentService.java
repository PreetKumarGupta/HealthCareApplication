package health.com.HealthCareApplication.service;


import health.com.HealthCareApplication.entity.Appointment;
import health.com.HealthCareApplication.entity.Doctor;
import health.com.HealthCareApplication.entity.Patient;
import health.com.HealthCareApplication.repository.AppointmentRepository;
import health.com.HealthCareApplication.repository.DoctorRepository;
import health.com.HealthCareApplication.repository.PatientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AppointmentService {

    private static final Logger log = LoggerFactory.getLogger(AppointmentService.class);

    // default slot length in minutes (can be moved to application.properties if desired)
    private static final long DEFAULT_SLOT_MINUTES = 30L;

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final NotificationService notificationService;

    public AppointmentService(AppointmentRepository appointmentRepository,
                              DoctorRepository doctorRepository,
                              PatientRepository patientRepository,
                              NotificationService notificationService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.notificationService = notificationService;
    }

    /**
     * Book an appointment for a patient with a doctor at a specific time.
     * Performs a simple availability check using a time window of +/- slot/2 minutes.
     *
     * @param doctorId  id of the doctor
     * @param patientId id of the patient
     * @param time      desired appointment time (LocalDateTime)
     * @return saved Appointment
     * @throws RuntimeException if doctor or patient not found or time conflict detected
     */
    @Transactional
    public Appointment bookAppointment(Long doctorId, Long patientId, LocalDateTime time) {
        if (doctorId == null || patientId == null || time == null) {
            throw new IllegalArgumentException("doctorId, patientId and appointment time must be provided");
        }

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(() -> new RuntimeException("Doctor not found with id: " + doctorId));
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new RuntimeException("Patient not found with id: " + patientId));

        // availability window: [time - halfSlot, time + halfSlot]
        long halfMinutes = DEFAULT_SLOT_MINUTES / 2;
        LocalDateTime windowStart = time.minusMinutes(halfMinutes);
        LocalDateTime windowEnd = time.plusMinutes(halfMinutes);

        List<Appointment> conflicts = appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, windowStart, windowEnd);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Doctor is not available at the requested time");
        }

        Appointment appt = new Appointment();
        appt.setDoctor(doctor);
        appt.setPatient(patient);
        appt.setAppointmentDateTime(time);
        appt.setStatus("BOOKED");


        Appointment saved = appointmentRepository.save(appt);
        log.info("Appointment booked: id={}, doctorId={}, patientId={}, time={}", saved.getId(), doctorId, patientId, time);

        // send notification (email fallback will print to console inside NotificationService)
        String message = String.format("Hi %s, your appointment with Dr. %s is booked for %s",
                patient.getName(), doctor.getName(), time);
        try {
            notificationService.sendEmail(patient.getEmail(), "Appointment booked", message);
        } catch (Exception ex) {
            // NotificationService already logs fallback; preserve transaction semantics
            log.warn("Failed to send notification for appointment id={}. Error: {}", saved.getId(), ex.getMessage());
        }

        return saved;
    }

    /**
     * Cancel an appointment by id. Marks the status as CANCELLED.
     *
     * @param appointmentId id of appointment to cancel
     * @return updated Appointment
     */
    @Transactional
    public Appointment cancelAppointment(Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
            return appt; // already cancelled
        }
        appt.setStatus("CANCELLED");
        Appointment updated = appointmentRepository.save(appt);

        // notify patient
        try {
            String message = String.format("Hi %s, your appointment with Dr. %s on %s has been cancelled.",
                    appt.getPatient().getName(), appt.getDoctor().getName(), appt.getAppointmentDateTime());
            notificationService.sendEmail(appt.getPatient().getEmail(), "Appointment cancelled", message);
        } catch (Exception ex) {
            log.warn("Failed to send cancellation notification for appointment id={}. Error: {}", appointmentId, ex.getMessage());
        }

        log.info("Appointment cancelled: id={}", appointmentId);
        return updated;
    }

    /**
     * Reschedule an appointment to a new time if the doctor is available.
     *
     * @param appointmentId id of existing appointment
     * @param newTime       new appointment time
     * @return updated Appointment
     */
    @Transactional
    public Appointment rescheduleAppointment(Long appointmentId, LocalDateTime newTime) {
        if (newTime == null) throw new IllegalArgumentException("newTime must be provided");

        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new RuntimeException("Appointment not found with id: " + appointmentId));

        if ("CANCELLED".equalsIgnoreCase(appt.getStatus())) {
            throw new RuntimeException("Cannot reschedule a cancelled appointment");
        }

        Long doctorId = appt.getDoctor().getId();
        long halfMinutes = DEFAULT_SLOT_MINUTES / 2;
        LocalDateTime windowStart = newTime.minusMinutes(halfMinutes);
        LocalDateTime windowEnd = newTime.plusMinutes(halfMinutes);

        // exclude current appointment from conflict check
        List<Appointment> conflicts = appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, windowStart, windowEnd);
        boolean hasConflict = conflicts.stream().anyMatch(c -> !c.getId().equals(appointmentId));
        if (hasConflict) {
            throw new RuntimeException("Doctor is not available at the new requested time");
        }

        LocalDateTime oldTime = (LocalDateTime) appt.getAppointmentDateTime();
        appt.setAppointmentDateTime(newTime);

        appt.setStatus("BOOKED");
        Appointment updated = appointmentRepository.save(appt);

        // notify patient
        try {
            String message = String.format("Hi %s, your appointment with Dr. %s has been rescheduled from %s to %s",
                    appt.getPatient().getName(), appt.getDoctor().getName(), oldTime, newTime);
            notificationService.sendEmail(appt.getPatient().getEmail(), "Appointment rescheduled", message);
        } catch (Exception ex) {
            log.warn("Failed to send reschedule notification for appointment id={}. Error: {}", appointmentId, ex.getMessage());
        }

        log.info("Appointment rescheduled: id={}, oldTime={}, newTime={}", appointmentId, oldTime, newTime);
        return updated;
    }

    /**
     * Get all appointments for a patient.
     *
     * @param patientId patient id
     * @return list of appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsForPatient(Long patientId) {
        if (patientId == null) throw new IllegalArgumentException("patientId must be provided");
        // repository method used earlier: findByPatientId
        return appointmentRepository.findByPatientId(patientId);
    }

    /**
     * Get appointments for a doctor in a time range.
     *
     * @param doctorId doctor id
     * @param start    range start
     * @param end      range end
     * @return list of appointments
     */
    @Transactional(readOnly = true)
    public List<Appointment> getAppointmentsForDoctorInRange(Long doctorId, LocalDateTime start, LocalDateTime end) {
        if (doctorId == null || start == null || end == null) {
            throw new IllegalArgumentException("doctorId, start and end must be provided");
        }
        return appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, start, end);
    }

    /**
     * Convenience method to check doctor's availability for a given time.
     *
     * @param doctorId doctor id
     * @param time     desired time
     * @return true if available
     */
    @Transactional(readOnly = true)
    public boolean isDoctorAvailable(Long doctorId, LocalDateTime time) {
        if (doctorId == null || time == null) throw new IllegalArgumentException("doctorId and time required");
        long halfMinutes = DEFAULT_SLOT_MINUTES / 2;
        LocalDateTime windowStart = time.minusMinutes(halfMinutes);
        LocalDateTime windowEnd = time.plusMinutes(halfMinutes);
        List<Appointment> conflicts = appointmentRepository.findByDoctorIdAndAppointmentDateTimeBetween(doctorId, windowStart, windowEnd);
        return conflicts.isEmpty();
    }
}