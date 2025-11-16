package health.com.HealthCareApplication.controller;


import health.com.HealthCareApplication.entity.Doctor;
import health.com.HealthCareApplication.repository.DoctorRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository){
        this.doctorRepository = doctorRepository;
    }

    // GET /api/doctors/all
    @GetMapping("/all")
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }

    // GET /api/doctors/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable Long id){
        return doctorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST /api/doctors/add
    @PostMapping("/add")
    public  ResponseEntity<Doctor> addDoctor(@RequestBody Doctor doctor){
        Doctor saved = doctorRepository.save(doctor);
        return ResponseEntity.ok(saved);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor dto){
        return doctorRepository.findById(id).map(existing->{
            existing.setName(dto.getName());
            existing.setSpecialization((dto.getSpecialization()));
            existing.setEmail(dto.getEmail());
            existing.setPhoneNumber(dto.getPhoneNumber());
            doctorRepository.save(existing);
            return ResponseEntity.ok(existing);
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?>deleteDoctor(@PathVariable Long id){
        if(!doctorRepository.existsById(id)) return ResponseEntity.notFound().build();
        doctorRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public List<Doctor>searchBySpecialization(@RequestParam("q") String q){
        return doctorRepository.findBySpecializationContainingIgnoreCase(q);
    }
}
