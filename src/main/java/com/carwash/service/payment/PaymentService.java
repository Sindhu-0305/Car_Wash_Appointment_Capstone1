package com.carwash.service.payment;

import org.springframework.stereotype.Service;

import com.carwash.dto.payment.PaymentPreviewResponse;
import com.carwash.dto.payment.PaymentResponse;

@Service
public interface PaymentService {

	PaymentPreviewResponse preview(String customerEmail, Long appointmentId, String offerCode);
    PaymentResponse pay(String customerEmail, Long appointmentId, String method, String offerCode);
//    void refundIfPaid(Long appointmentId, String reason);
//    PaymentResponse getByAppointment(String customerEmail, Long appointmentId);


}
