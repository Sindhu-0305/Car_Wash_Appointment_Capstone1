
package com.carwash.service.feedback;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carwash.dto.feedback.FeedbackCreateRequest;
import com.carwash.dto.feedback.FeedbackResponse;
import com.carwash.dto.feedback.ProviderFeedbackSummary;
import com.carwash.entity.ServiceProvider;
import com.carwash.entity.User;
import com.carwash.entity.appointment.Appointment;
import com.carwash.entity.feedback.Feedback;
import com.carwash.enums.appointment.BookingStatus;
import com.carwash.exception.AccessDeniedCustomException;
import com.carwash.exception.BadRequestException;
import com.carwash.exception.ConflictException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.ServiceProviderRepository;
import com.carwash.repository.UserRepository;
import com.carwash.repository.appointment.AppointmentRepository;
import com.carwash.repository.feedback.FeedbackRepository;

@Service
public class FeedbackServiceImpl implements FeedbackService {


    private static final Logger log = LoggerFactory.getLogger(FeedbackServiceImpl.class);

    @Autowired
    private FeedbackRepository feedbackRepo;

    @Autowired
    private AppointmentRepository apptRepo;

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private ServiceProviderRepository providerRepo;

    @Override
    public FeedbackResponse createFeedback(String customerEmail, FeedbackCreateRequest req) {
    	 log.info("Creating feedback for customerEmail={} appointmentId={}", customerEmail, req.getAppointmentId());
    	User customer = userRepo.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        Appointment appt = apptRepo.findById(req.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment not found"));

               if (appt.getCustomer() == null ||
            !appt.getCustomer().getUserId().equals(customer.getUserId())) {
            throw new AccessDeniedCustomException("You can only give feedback for your own appointment");
        }

        if (appt.getStatus() != BookingStatus.COMPLETED) {
            throw new BadRequestException("Feedback allowed only after appointment is COMPLETED");
        }

                if (feedbackRepo.existsByAppointmentId(appt.getId())) {
            throw new ConflictException("Feedback already submitted for this appointment");
        }

        ServiceProvider provider = appt.getServiceProvider();
        if (provider == null) {
            throw new BadRequestException("Appointment has no service provider");
        }

        Feedback f = new Feedback();
        f.setAppointment(appt);
        f.setServiceProvider(provider);
        f.setCustomer(customer);
        f.setRating(req.getRating());
        f.setComment(req.getComment());

        Feedback saved = feedbackRepo.save(f);

        
        refreshProviderRating(provider.getId());

        return toResponse(saved);
    }

    @Override
    public List<FeedbackResponse> getMyFeedbacks(String customerEmail) {
    	 log.info("Fetching my feedbacks for customerEmail={}", customerEmail);
    	User customer = userRepo.findByEmail(customerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        return feedbackRepo.findByCustomerUserId(customer.getUserId())
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getByProvider(Long providerId) {
    	log.info("Fetching feedbacks by providerId={}", providerId);
    	return feedbackRepo.findByServiceProviderId(providerId)
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<FeedbackResponse> getAll() {

        log.info("Fetching all feedbacks");

        return feedbackRepo.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ProviderFeedbackSummary getProviderRatingSummary(Long providerId) {
    	log.info("Computing rating summary for providerId={}", providerId);
    	List<Feedback> list = feedbackRepo.findByServiceProviderId(providerId);

        if (list.isEmpty()) {
            return new ProviderFeedbackSummary(providerId, 0.0, 0L);
        }

        double avg = list.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

        long total = list.size();

        return new ProviderFeedbackSummary(providerId, round1(avg), total);
    }

    @Override
    public void refreshProviderRating(Long providerId) {
    	log.info("Refreshing provider rating providerId={}", providerId);
    	List<Feedback> list = feedbackRepo.findByServiceProviderId(providerId);

        double avg = list.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0.0);

               providerRepo.findById(providerId).ifPresent(p -> {
            p.setRating(round1(avg));
            providerRepo.save(p);

            log.info("Provider rating updated providerId={} rating={}", providerId, round1(avg));

        });
    }

    private FeedbackResponse toResponse(Feedback f) {
        return new FeedbackResponse(
                f.getId(),                              
                f.getAppointment().getId(),             
                f.getServiceProvider().getId(),         
                f.getCustomer().getUserId(),            
                f.getRating(),
                f.getComment(),
                f.getCreatedAt()
        );
    }

    private double round1(Double value) {
        return Math.round(value * 10.0) / 10.0;
    }
}
