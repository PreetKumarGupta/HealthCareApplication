package health.com.HealthCareApplication.repository;

import health.com.HealthCareApplication.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByDoctorIdAndAppointmentDateTimeBetween(Long doctorId, LocalDateTime startTime, LocalDateTime endTime);
    List<Appointment> findByPatientId(Long patientId);
}
