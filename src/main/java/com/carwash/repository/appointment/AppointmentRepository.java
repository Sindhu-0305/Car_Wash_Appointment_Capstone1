package com.carwash.repository.appointment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carwash.entity.ServiceProvider;
import com.carwash.entity.User;
import com.carwash.entity.appointment.Appointment;
import com.carwash.enums.appointment.BookingStatus;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

	List<Appointment> findByCustomer(User customer);

	List<Appointment> findByServiceProvider(ServiceProvider provider);

	List<Appointment> findByServiceProviderAndStatusIn(ServiceProvider provider, List<BookingStatus> statuses);
}
