# HealthCareApplication
Healthcare backend app built with Spring Boot and MySQL. Manages doctors, patients, and appointments with features like booking, rescheduling, cancellation, availability checks, and notifications.


#HealthCareApplication is a complete Spring Boot–based backend system designed to manage healthcare operations such as doctors, patients, and appointment scheduling. The application follows a clean 3-layer architecture (Controller → Service → Repository) and uses Spring Boot, Spring Data JPA, MySQL, and Lombok to provide a robust and maintainable solution.

✨The system allows patients to register, view doctors, and book/reschedule/cancel appointments while ensuring that doctor availability is validated in real-time. It also includes a notification service that sends appointment confirmation emails or console fallback messages.

📚Key Features

Doctor Management
Add, update, delete, and search doctors by specialization.

Patient Management
Register patients, update details, and fetch patient information.

Appointment Management

Book appointments with availability validation

Reschedule or cancel appointments

Fetch appointments by patient or doctor

Check doctor availability for a specific time

✨Notification System
Sends appointment confirmations through email or logs fallback notifications.

✨Clean Architecture
Proper separation of layers for better readability, testability, and maintenance.

✨Error Handling
Centralized exception handling with meaningful error responses.



🛠️ Tech Stack

Java 17+

Spring Boot (Web, JPA, Validation)

Spring Data JPA (Hibernate)

MySQL

Lombok

Postman for API testing

📚 API Highlights

POST /api/doctors – Add doctor

GET /api/doctors/search?q= – Search specialization

POST /api/appointments/book – Book appointment

POST /api/appointments/{id}/reschedule – Reschedule

GET /api/appointments/doctor/{id}/available – Check availability

🎯 Purpose

This project demonstrates real-world backend development skills using Spring Boot and provides a foundation for building scalable healthcare systems.
