package com.carwash.service.appointment;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.carwash.dto.appointment.AppointmentRequest;
import com.carwash.dto.appointment.AppointmentResponse;
import com.carwash.dto.appointment.ServiceProviderAvailability;

@Service
public interface AppointmentService {

	List<ServiceProviderAvailability> recentProvidersOptions(String customerEmail, Long serviceItemId, LocalDateTime scheduledAt);
    List<ServiceProviderAvailability> suggestedProviders(String customerEmail, Long serviceItemId, LocalDateTime scheduledAt, Integer limit);

    AppointmentResponse create(String customerEmail, AppointmentRequest req);

    List<AppointmentResponse> getMyBookings(String customerEmail);
    void cancelMyBooking(String customerEmail, Long appointmentId);

    List<AppointmentResponse> getProviderBookings(String providerEmail);
    void accept(String providerEmail, Long appointmentId);
    void reject(String providerEmail, Long appointmentId);

    List<AppointmentResponse> getAll();
    AppointmentResponse getById(Long id);
    void updateStatus(Long id, String status);
	void complete(String name, Long id);

}
