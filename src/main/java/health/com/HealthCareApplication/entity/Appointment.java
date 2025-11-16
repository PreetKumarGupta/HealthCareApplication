package health.com.HealthCareApplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    @ManyToOne(optional = false)
    @JoinColumn(name = "patient_id")
    private Patient patient;

    // main appointment time
    @Column(name = "appointment_date_time")
    private LocalDateTime appointmentDateTime;

    // status: BOOKED, CANCELLED, COMPLETED
    private String status;

    // Constructors
    public Appointment() {}

    public Appointment(Long id, Doctor doctor, Patient patient, LocalDateTime appointmentDateTime, String status) {
        this.id = id;
        this.doctor = doctor;
        this.patient = patient;
        this.appointmentDateTime = appointmentDateTime;
        this.status = status;
    }
    //dummy commit

    // --- standard getters / setters ------------------------------------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    // --- compatibility aliases (some code expects getAppointmentTime()) -
    // keep both names so other code will compile
    public LocalDateTime getAppointmentTime() {
        return getAppointmentDateTime();
    }

    public void setAppointmentTime(LocalDateTime appointmentTime) {
        setAppointmentDateTime(appointmentTime);
    }

    // --- convenience helper methods -----------------------------------

    public String getDoctorName() {
        return doctor != null ? doctor.getName() : null;
    }

    public String getPatientName() {
        return patient != null ? patient.getName() : null;
    }

    public String getPatientEmail() {
        return patient != null ? patient.getEmail() : null;
    }

    // keep legacy getEmail() name (some code used getEmail())
    public String getEmail() {
        return getPatientEmail();
    }
}