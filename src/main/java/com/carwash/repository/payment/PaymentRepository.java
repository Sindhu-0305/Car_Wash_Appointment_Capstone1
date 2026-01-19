package com.carwash.repository.payment;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carwash.entity.appointment.Appointment;
import com.carwash.entity.payment.Payment;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

	Optional<Payment> findByAppointment(Appointment appt);
    boolean existsByAppointment(Appointment appt);
}
