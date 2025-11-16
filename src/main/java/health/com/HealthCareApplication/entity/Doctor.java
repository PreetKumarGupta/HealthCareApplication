package health.com.HealthCareApplication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "doctors")

public class Doctor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String specialization;
    private String email;
    private String phoneNumber;

    public String getName() {
        return this.name;
    }
    public Long getId() {
        return this.id;
    }

    public Doctor setName(String name) {
        this.name = name;
        return this;
    }

    public String getSpecialization() {
        return specialization;
    }

    public Doctor setSpecialization(String specialization) {
        this.specialization = specialization;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Doctor setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Doctor setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    // this project is for healthcare management
}
