package com.carwash.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.carwash.dto.appointment.AppointmentResponse;
import com.carwash.entity.User;
import com.carwash.entity.appointment.Appointment;
import com.carwash.enums.Role;
import com.carwash.enums.appointment.BookingStatus;
import com.carwash.repository.UserRepository;
import com.carwash.repository.appointment.AppointmentRepository;
import com.carwash.service.appointment.AppointmentServiceImpl;

class AppointmentServiceImplTest {

	@InjectMocks
	private AppointmentServiceImpl service;

	@Mock
	private AppointmentRepository appointmentRepo;

	@Mock
	private UserRepository userRepo;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void testUpdateStatus() {

		Appointment appt = new Appointment();
		appt.setId(10L);
		appt.setStatus(BookingStatus.PENDING);

		when(appointmentRepo.findById(10L)).thenReturn(Optional.of(appt));

		service.updateStatus(10L, "ACCEPTED");

		assertEquals(BookingStatus.ACCEPTED, appt.getStatus());
	}

}
