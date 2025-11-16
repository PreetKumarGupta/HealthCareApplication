package health.com.HealthCareApplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;


public class AppointmentRequest {
    @Getter
    private Long doctorId;
    private Long patientId;
    private LocalDateTime appointmentDateTime;

public AppointmentRequest(){

}

public AppointmentRequest(Long doctorId,Long patientId,LocalDateTime appointmentDateTime){
    this.doctorId=doctorId;
    this.patientId=patientId;
    this.appointmentDateTime=appointmentDateTime;
}
    public Long getDoctorId() {
        return doctorId;
    }
    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    public Long getPatientId() {
        return patientId;
    }
    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }
    public LocalDateTime getAppointmentDateTime() {
        return appointmentDateTime;
    }
    public void setAppointmentDateTime(LocalDateTime appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }


}
