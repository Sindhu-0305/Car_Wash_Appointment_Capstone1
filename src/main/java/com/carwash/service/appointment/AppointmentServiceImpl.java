package com.carwash.service.appointment;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carwash.dto.appointment.AppointmentRequest;
import com.carwash.dto.appointment.AppointmentResponse;
import com.carwash.dto.appointment.ServiceProviderAvailability;
import com.carwash.entity.ServiceProvider;
import com.carwash.entity.User;
import com.carwash.entity.appointment.Appointment;
import com.carwash.entity.scatalog.ServiceItem;
import com.carwash.enums.Role;
import com.carwash.enums.appointment.BookingStatus;
import com.carwash.enums.payment.PaymentStatus;
import com.carwash.exception.AccessDeniedCustomException;
import com.carwash.exception.BadRequestException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.ServiceProviderRepository;
import com.carwash.repository.UserRepository;
import com.carwash.repository.appointment.AppointmentRepository;
import com.carwash.repository.payment.PaymentRepository;
import com.carwash.repository.scatalog.ServiceItemRepository;
import com.carwash.service.payment.PaymentService;

@Service
public class AppointmentServiceImpl implements AppointmentService {

	@Autowired
	private AppointmentRepository appointmentRepo;

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private ServiceProviderRepository serviceProviderRepo;

	@Autowired
	private ServiceItemRepository serviceItemRepo;

	@Autowired
	private PaymentService paymentService;

	@Autowired
	private PaymentRepository paymentRepo;

	@Override
	public List<ServiceProviderAvailability> recentProvidersOptions(String customerEmail, Long serviceItemId,
			LocalDateTime scheduledAt) {

		User customer = userRepo.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		if (customer.getRole() != Role.CUSTOMER) {
			throw new AccessDeniedCustomException("Only customers allowed");
		}

		ServiceItem item = null;
		if (serviceItemId != null) {
			item = serviceItemRepo.findById(serviceItemId).orElse(null);
		}

		List<ServiceProvider> past = findPastProviders(customer);
		List<ServiceProviderAvailability> result = new ArrayList<>();

		for (ServiceProvider p : past) {
			boolean available = true;

			if (scheduledAt != null && item != null) {
				Integer duration = item.getDurationMinutes();
				available = isProviderAvailable(p, scheduledAt, duration);
			}

			result.add(toSummary(p, available));
		}

		return result;

	}

	@Override
	public List<ServiceProviderAvailability> suggestedProviders(String customerEmail, Long serviceItemId,
			LocalDateTime scheduledAt, Integer limit) {
		// TODO Auto-generated method stub
		if (limit == null) {
			limit = 5;
		}
		if (limit <= 0) {
			limit = 5;
		}

		User customer = userRepo.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		if (customer.getRole() != Role.CUSTOMER) {
			throw new AccessDeniedCustomException("Only customers allowed");
		}

		ServiceItem item = serviceItemRepo.findById(serviceItemId)
				.orElseThrow(() -> new ResourceNotFoundException("Service item not found"));

		List<ServiceProvider> past = findPastProviders(customer);
		Set<Long> pastIds = past.stream().map(ServiceProvider::getId).collect(Collectors.toSet());

		List<ServiceProviderAvailability> availableOthers = new ArrayList<>();

		for (ServiceProvider p : serviceProviderRepo.findAll()) {
			if (pastIds.contains(p.getId())) {
				continue;
			}

			boolean available = isProviderAvailable(p, scheduledAt, item.getDurationMinutes());
			if (available) {
				availableOthers.add(toSummary(p, true));
			}

			if (availableOthers.size() >= limit) {
				break;
			}
		}

		return availableOthers;

	}

	@Override
	public AppointmentResponse create(String customerEmail, AppointmentRequest req) {
		// TODO Auto-generated method stub
		User customer = userRepo.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

		if (customer.getRole() != Role.CUSTOMER) {
			throw new AccessDeniedCustomException("Only customers can book");
		}

		ServiceProvider provider = serviceProviderRepo.findById(req.getProviderId())
				.orElseThrow(() -> new ResourceNotFoundException("Provider not found"));

		ServiceItem item = serviceItemRepo.findById(req.getServiceItemId())
				.orElseThrow(() -> new ResourceNotFoundException("Service item not found"));

		List<ServiceProvider> past = findPastProviders(customer);

		if (!past.isEmpty()) {
			boolean chosenIsPast = false;
			for (ServiceProvider p : past) {
				if (p.getId().equals(provider.getId())) {
					chosenIsPast = true;
					break;
				}
			}

			List<ServiceProvider> availablePast = new ArrayList<>();
			for (ServiceProvider p : past) {
				boolean available = isProviderAvailable(p, req.getScheduledAt(), item.getDurationMinutes());
				if (available) {
					availablePast.add(p);
				}
			}

			if (!chosenIsPast && !availablePast.isEmpty()) {
				throw new BadRequestException("Choose a provider you used before at this time");
			}

			if (!availablePast.isEmpty() && chosenIsPast) {
				boolean chosenAvailable = isProviderAvailable(provider, req.getScheduledAt(),
						item.getDurationMinutes());
				if (!chosenAvailable) {
					throw new BadRequestException("Selected past provider is busy at this time");
				}
			}

			if (availablePast.isEmpty()) {
				boolean chosenAvailable = isProviderAvailable(provider, req.getScheduledAt(),
						item.getDurationMinutes());
				if (!chosenAvailable) {
					throw new BadRequestException("All your past providers are busy. Check suggestions and try again");
				}
			}
		} else {
			boolean chosenAvailable = isProviderAvailable(provider, req.getScheduledAt(), item.getDurationMinutes());
			if (!chosenAvailable) {
				throw new BadRequestException("Provider is busy at the requested time");
			}
		}

		Appointment appt = new Appointment();
		appt.setCustomer(customer);
		appt.setServiceProvider(provider);
		appt.setServiceItem(item);
		appt.setScheduledAt(req.getScheduledAt());
		appt.setDurationMinutes(item.getDurationMinutes());
		appt.setPrice(item.getPrice());
		appt.setStatus(BookingStatus.PENDING);
		appt.setNotes(req.getNotes());

		appt = appointmentRepo.save(appt);

		return toResponse(appt);
	}

	@Override
	public List<AppointmentResponse> getMyBookings(String customerEmail) {
		// TODO Auto-generated method stub
		User customer = userRepo.findByEmail(customerEmail)
				.orElseThrow(() -> new RuntimeException("Customer not found"));

		if (customer.getRole() != Role.CUSTOMER) {
			throw new RuntimeException("Only customers allowed");
		}

		List<Appointment> list = appointmentRepo.findByCustomer(customer);
		List<AppointmentResponse> out = new ArrayList<>();

		for (Appointment a : list) {
			out.add(toResponse(a));
		}

		return out;

	}

	@Override
	public void cancelMyBooking(String customerEmail, Long appointmentId) {

		User customer = userRepo.findByEmail(customerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("Customer not found"));
		if (customer.getRole() != Role.CUSTOMER) {
			throw new AccessDeniedCustomException("Only customers allowed");
		}
		Appointment appt = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appt.getCustomer().getUserId().equals(customer.getUserId())) {
			throw new AccessDeniedCustomException("You cannot cancel this booking");
		}
		if (appt.getStatus() == BookingStatus.COMPLETED) {
			throw new BadRequestException("Cannot cancel completed booking");
		}

	}

	@Override
	public List<AppointmentResponse> getProviderBookings(String providerEmail) {
		// TODO Auto-generated method stub
		User providerUser = userRepo.findByEmail(providerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		ServiceProvider provider = null;
		for (ServiceProvider p : serviceProviderRepo.findAll()) {
			if (p.getUser() != null && p.getUser().getUserId().equals(providerUser.getUserId())) {
				provider = p;
				break;
			}
		}

		if (provider == null) {
			throw new ResourceNotFoundException("Service provider not found");
		}

		List<Appointment> list = appointmentRepo.findByServiceProvider(provider);
		List<AppointmentResponse> out = new ArrayList<>();

		for (Appointment a : list) {
			out.add(toResponse(a));
		}

		return out;

	}

	@Override
	public void accept(String providerEmail, Long appointmentId) {
		Appointment appt = findProviderOwnedAppointment(providerEmail, appointmentId);

		if (appt.getStatus() != BookingStatus.PENDING) {
			throw new BadRequestException("Only pending can be accepted");
		}

		boolean paid = isPaid(appointmentId);
		if (!paid) {
			throw new BadRequestException("Payment required before accepting the booking");
		}

		appt.setStatus(BookingStatus.ACCEPTED);
		appointmentRepo.save(appt);
	}

	@Override
	public void reject(String providerEmail, Long appointmentId) {
		// TODO Auto-generated method stub

		Appointment appt = findProviderOwnedAppointment(providerEmail, appointmentId);
		if (appt.getStatus() != BookingStatus.PENDING) {
			throw new BadRequestException("Only pending can be rejected");
		}
		appt.setStatus(BookingStatus.REJECTED);
		appointmentRepo.save(appt);

	}

	@Override
	public List<AppointmentResponse> getAll() {
		// TODO Auto-generated method stub
		List<Appointment> list = appointmentRepo.findAll();
		List<AppointmentResponse> out = new ArrayList<>();
		for (Appointment a : list) {
			out.add(toResponse(a));
		}
		return out;

	}

	@Override
	public AppointmentResponse getById(Long id) {
		// TODO Auto-generated method stub
		Appointment appt = appointmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));
		return toResponse(appt);

	}

	@Override
	public void updateStatus(Long id, String status) {
		// TODO Auto-generated method stub

		Appointment appt = appointmentRepo.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		BookingStatus newStatus;
		try {
			newStatus = BookingStatus.valueOf(status.toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new BadRequestException("Invalid status: " + status);
		}

		appt.setStatus(newStatus);
		appointmentRepo.save(appt);

	}

	private List<ServiceProvider> findPastProviders(User customer) {
		List<Appointment> appts = appointmentRepo.findByCustomer(customer);
		Set<Long> ids = new LinkedHashSet<>();

		for (Appointment a : appts) {
			boolean hasProvider = a.getServiceProvider() != null;
			boolean isPastStatus = a.getStatus() == BookingStatus.ACCEPTED || a.getStatus() == BookingStatus.COMPLETED;

			if (hasProvider && isPastStatus) {
				ids.add(a.getServiceProvider().getId());
			}
		}

		List<ServiceProvider> result = new ArrayList<>();
		for (ServiceProvider p : serviceProviderRepo.findAll()) {
			if (ids.contains(p.getId())) {
				result.add(p);
			}
		}

		return result;

	}

	private boolean isProviderAvailable(ServiceProvider provider, LocalDateTime start, Integer durationMinutes) {
		if (start == null) {
			return false;
		}
		if (durationMinutes == null) {
			return false;
		}

		LocalDateTime end = start.plusMinutes(durationMinutes);

		List<BookingStatus> active = Arrays.asList(BookingStatus.PENDING, BookingStatus.ACCEPTED);
		List<Appointment> appts = appointmentRepo.findByServiceProviderAndStatusIn(provider, active);

		for (Appointment a : appts) {
			LocalDateTime aStart = a.getScheduledAt();

			int mins = 0;
			if (a.getDurationMinutes() != null) {
				mins = a.getDurationMinutes();
			}
			LocalDateTime aEnd = aStart.plusMinutes(mins);

			boolean startsBeforeEnd = aStart.isBefore(end);
			boolean endsAfterStart = aEnd.isAfter(start);
			boolean overlap = startsBeforeEnd && endsAfterStart;

			if (overlap) {
				return false;
			}
		}

		return true;
	}

	private Appointment findProviderOwnedAppointment(String providerEmail, Long appointmentId) {
		User providerUser = userRepo.findByEmail(providerEmail)
				.orElseThrow(() -> new ResourceNotFoundException("User not found"));

		ServiceProvider provider = null;
		for (ServiceProvider p : serviceProviderRepo.findAll()) {
			boolean hasUser = p.getUser() != null;
			if (hasUser && p.getUser().getUserId().equals(providerUser.getUserId())) {
				provider = p;
				break;
			}
		}

		if (provider == null) {
			throw new ResourceNotFoundException("Service provider not found");
		}

		Appointment appt = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		if (!appt.getServiceProvider().getId().equals(provider.getId())) {
			throw new AccessDeniedCustomException("Not your booking");
		}

		return appt;
	}

	private ServiceProviderAvailability toSummary(ServiceProvider sp, boolean available) {
		ServiceProviderAvailability ps = new ServiceProviderAvailability();

		User u = sp.getUser();
		ps.setProviderId(sp.getId());

		if (u != null) {
			ps.setFullName(u.getFullName());
			ps.setEmail(u.getEmail());
			ps.setPhoneNumber(u.getPhoneNumber());
		} else {
			ps.setFullName(null);
			ps.setEmail(null);
			ps.setPhoneNumber(null);
		}

		ps.setSpecialization(sp.getSpecialization());
		ps.setExperienceYears(sp.getExperienceYears());
		ps.setAvailable(available);

		return ps;
	}

	private AppointmentResponse toResponse(Appointment a) {
		AppointmentResponse r = new AppointmentResponse();

		r.setId(a.getId());
		r.setCustomerId(a.getCustomer().getUserId());
		r.setProviderId(a.getServiceProvider().getId());
		r.setServiceItemId(a.getServiceItem().getId());
		r.setServiceCode(a.getServiceItem().getCode());
		r.setServiceName(a.getServiceItem().getName());
		r.setPrice(a.getPrice());
		r.setDurationMinutes(a.getDurationMinutes());
		r.setScheduledAt(a.getScheduledAt());
		r.setStatus(a.getStatus());
		r.setNotes(a.getNotes());

		return r;
	}

	@Override
	public void complete(String providerEmail, Long appointmentId) {
		Appointment appt = findProviderOwnedAppointment(providerEmail, appointmentId);

		if (appt.getStatus() != BookingStatus.ACCEPTED) {
			throw new RuntimeException("Only accepted bookings can be completed");
		}

		appt.setStatus(BookingStatus.COMPLETED);
		appointmentRepo.save(appt);
	}

	private boolean isPaid(Long appointmentId) {
		Appointment appt = appointmentRepo.findById(appointmentId)
				.orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

		return paymentRepo.findByAppointment(appt).map(p -> p.getStatus() == PaymentStatus.PAID).orElse(false);
	}

}
