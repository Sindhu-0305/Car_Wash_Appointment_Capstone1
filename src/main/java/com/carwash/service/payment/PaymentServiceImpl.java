package com.carwash.service.payment;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carwash.dto.offer.OfferResponse;
import com.carwash.dto.payment.PaymentPreviewResponse;
import com.carwash.dto.payment.PaymentResponse;
import com.carwash.entity.User;
import com.carwash.entity.appointment.Appointment;
import com.carwash.entity.payment.Payment;
import com.carwash.enums.appointment.BookingStatus;
import com.carwash.enums.offer.DiscountType;
import com.carwash.enums.payment.PaymentStatus;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.UserRepository;
import com.carwash.repository.appointment.AppointmentRepository;
import com.carwash.repository.payment.PaymentRepository;
import com.carwash.service.offer.OfferService;

@Service
public class PaymentServiceImpl implements PaymentService {

	@Autowired
	private AppointmentRepository appointmentRepository;
	@Autowired
	private PaymentRepository paymentRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private OfferService offerService;

	@Override
	public PaymentPreviewResponse preview(String customerEmail, Long appointmentId, String offerCode) {
		// TODO Auto-generated method stub
		Appointment appt = mustBeCustomerOwnedAppt(customerEmail, appointmentId);

		Double amount = appt.getPrice();
		Double discount = 0.0;

		if (offerCode != null && !offerCode.isBlank()) {
			discount = computeDiscount(amount, offerCode);
		}

		PaymentPreviewResponse res = new PaymentPreviewResponse();
		res.setAmount(amount);
		res.setDiscount(discount);
		res.setFinalAmount(Math.max(0.0, amount - discount));
		res.setOfferCode(offerCode);
		return res;
	}

	@Override
	public PaymentResponse pay(String customerEmail, Long appointmentId, String method, String offerCode) {
		// TODO Auto-generated method stub
		Appointment appt = mustBeCustomerOwnedAppt(customerEmail, appointmentId);

		if (paymentRepository.existsByAppointment(appt)) {
			Payment existing = paymentRepository.findByAppointment(appt).get();
			if (existing.getStatus() == PaymentStatus.PAID) {
				return toResponse(existing);
			}
		}

		Double amount = appt.getPrice();
		Double discount = 0.0;

		if (offerCode != null && !offerCode.isBlank()) {
			discount = computeDiscount(amount, offerCode);
		}

		Double finalAmount = Math.max(0.0, amount - discount);

		Payment p = new Payment();
		p.setAppointment(appt);
		p.setAmount(amount);
		p.setDiscount(discount);
		p.setFinalAmount(finalAmount);
		p.setOfferCode(offerCode);
		p.setMethod(method);
		p.setStatus(PaymentStatus.PAID);

		p = paymentRepository.save(p);

		return toResponse(p);
	}

	private Appointment mustBeCustomerOwnedAppt(String customerEmail, Long appointmentId) {
		User user = userRepository.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		Appointment appt = appointmentRepository.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appt.getCustomer().getUserId().equals(user.getUserId())) {
			throw new ResourceNotFoundException("Not your appointment");
		}
		if (appt.getStatus() == BookingStatus.CANCELLED || appt.getStatus() == BookingStatus.REJECTED) {
			throw new ResourceNotFoundException("Cannot pay for cancelled/rejected booking");
		}
		return appt;
	}

	private Double computeDiscount(Double amount, String code) {
		OfferResponse offer = offerService.getByCode(code);

		LocalDate today = LocalDate.now();
		boolean dateOk = (offer.getStartDate() == null || !today.isBefore(offer.getStartDate()))
				&& (offer.getEndDate() == null || !today.isAfter(offer.getEndDate()));
		if (!dateOk || offer.getActive() == null || !offer.getActive()) {
			return 0.0;
		}

		if (offer.getMinOrderAmount() != null && amount < offer.getMinOrderAmount()) {
			return 0.0;
		}

		Double discount = 0.0;
		if (offer.getDiscountType() == DiscountType.PERCENTAGE) {
			discount = amount * (offer.getDiscountValue() / 100.0);
		} else {
			discount = offer.getDiscountValue();
		}

		if (offer.getMaxDiscountAmount() != null && discount > offer.getMaxDiscountAmount()) {
			discount = offer.getMaxDiscountAmount();
		}

		if (discount < 0)
			discount = 0.0;
		if (discount > amount)
			discount = amount;

		return discount;
	}

	private PaymentResponse toResponse(Payment p) {
		PaymentResponse r = new PaymentResponse();
		r.setPaymentId(p.getId());
		r.setAppointmentId(p.getAppointment().getId());
		r.setAmount(p.getAmount());
		r.setDiscount(p.getDiscount());
		r.setFinalAmount(p.getFinalAmount());
		r.setStatus(p.getStatus());
		r.setOfferCode(p.getOfferCode());
		r.setMethod(p.getMethod());
		return r;
	}



}
