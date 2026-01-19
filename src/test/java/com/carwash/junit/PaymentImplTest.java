package com.carwash.junit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.carwash.dto.offer.OfferResponse;
import com.carwash.dto.payment.PaymentPreviewResponse;
import com.carwash.dto.payment.PaymentResponse;
import com.carwash.entity.User;
import com.carwash.entity.appointment.Appointment;
import com.carwash.entity.payment.Payment;
import com.carwash.enums.appointment.BookingStatus;
import com.carwash.enums.offer.DiscountType;
import com.carwash.enums.payment.PaymentStatus;
import com.carwash.repository.UserRepository;
import com.carwash.repository.appointment.AppointmentRepository;
import com.carwash.repository.payment.PaymentRepository;
import com.carwash.service.offer.OfferService;
import com.carwash.service.payment.PaymentServiceImpl;

class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service;

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OfferService offerService;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

  
    @Test
    void testPaySimple() {

        User customer = new User();
        customer.setUserId(1L);
        customer.setEmail("yuva@gmail.com");

        Appointment appt = new Appointment();
        appt.setId(200L);
        appt.setCustomer(customer);
        appt.setStatus(BookingStatus.PENDING);
        appt.setPrice(300.0);

        Payment saved = new Payment();
        saved.setId(50L);
        saved.setAppointment(appt);
        saved.setAmount(300.0);
        saved.setDiscount(0.0);
        saved.setFinalAmount(300.0);
        saved.setStatus(PaymentStatus.PAID);

        when(userRepository.findByEmail("yuva@gmail.com")).thenReturn(Optional.of(customer));
        when(appointmentRepository.findById(200L)).thenReturn(Optional.of(appt));
        when(paymentRepository.existsByAppointment(appt)).thenReturn(false);
        when(paymentRepository.save(any(Payment.class))).thenReturn(saved);

        PaymentResponse res = service.pay("yuva@gmail.com", 200L, "CARD", null);

        assertEquals(50L, res.getPaymentId());
        assertEquals(300.0, res.getAmount());
        assertEquals(0.0, res.getDiscount());
        assertEquals(300.0, res.getFinalAmount());
        assertEquals(PaymentStatus.PAID, res.getStatus());
    }
}

