package com.carwash.repository.feedback;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.carwash.entity.feedback.Feedback;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

	boolean existsByAppointmentId(Long appointmentId);

	Optional<Feedback> findByAppointmentId(Long appointmentId);

	List<Feedback> findByCustomerUserId(Long userId);

	List<Feedback> findByServiceProviderId(Long providerId);
}
