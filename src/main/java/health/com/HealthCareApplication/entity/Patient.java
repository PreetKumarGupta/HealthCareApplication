package health.com.HealthCareApplication.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "patients")
public class Patient {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String email;
    private String phoneNumber;
    private  String age;

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.email;
    }


    public Long getId() {
        return id;
    }

    public Patient setId(Long id) {
        this.id = id;
        return this;
    }

    public Patient setName(String name) {
        this.name = name;
        return this;
    }

    public Patient setEmail(String email) {
        this.email = email;
        return this;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public Patient setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
        return this;
    }

    public String getAge() {
        return age;
    }

    public Patient setAge(String age) {
        this.age = age;
        return this;
    }
}
