package com.carwash.service.offer;

import java.time.LocalDate;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carwash.dto.offer.OfferRequest;
import com.carwash.dto.offer.OfferResponse;
import com.carwash.entity.offer.Offer;
import com.carwash.exception.BadRequestException;
import com.carwash.exception.ConflictException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.offer.OfferRepository;

@Service
public class OfferServiceImpl implements OfferService {

	private static final Logger log = LoggerFactory.getLogger(OfferServiceImpl.class);
	@Autowired
	private OfferRepository offerRepo;

	@Override
	public OfferResponse create(OfferRequest req) {
		log.info("Creating offer code={}", req.getCode());
		validate(req, true);
		if (offerRepo.existsByCode(req.getCode())) {
			log.warn("Offer creation failed — code already exists code={}", req.getCode());

			throw new ConflictException("Code already exists");
		}
			Offer o = requestToOffer(new Offer(), req);
		o.setActive(true);
		o = offerRepo.save(o);
		return offertoResponse(o);
	}

	@Override
	public OfferResponse update(Long id, OfferRequest req) {
		log.info("Updating offer id={}", id);
		Offer o = offerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
		if (req.getCode() != null && !req.getCode().equals(o.getCode())) {
			if (offerRepo.existsByCode(req.getCode()))
				throw new ConflictException("Code already exists");
		}
		validate(req, false);
		o = requestToOffer(o, req);
		o = offerRepo.save(o);
		log.info("Offer updated id={}", id);
		return offertoResponse(o);
	}

	@Override
	public void delete(Long id) {

        log.info("Deleting offer id={}", id);

		offerRepo.deleteById(id);

        log.info("Offer deleted id={}", id);

	}

	@Override
	public void activate(Long id) {
		Offer o = offerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
		o.setActive(true);
		offerRepo.save(o);

        log.info("Offer activated id={}", id);

	}

	@Override
	public void deactivate(Long id) {
		Offer o = offerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
		o.setActive(false);
		offerRepo.save(o);

        log.info("Offer deactivated id={}", id);

        
	}

	@Override
	public OfferResponse getById(Long id) {

        log.info("Fetching offer by id={}", id);

		Offer o = offerRepo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
		return offertoResponse(o);
	}

	@Override
	public List<OfferResponse> getAll(boolean onlyActive, boolean onlyRunningToday) {
		log.info("Listing offers onlyActive={} onlyRunningToday={}", onlyActive, onlyRunningToday);
		if (onlyRunningToday) {
			LocalDate today = LocalDate.now();
			return offerRepo.findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(today, today).stream()
					.map(this::offertoResponse).toList();
		}
		if (onlyActive) {
			return offerRepo.findByActiveTrue().stream().map(this::offertoResponse).toList();
		}
		return offerRepo.findAll().stream().map(this::offertoResponse).toList();
	}

	@Override
	public OfferResponse getByCode(String code) {
		return offerRepo.findAll().stream().filter(o -> o.getCode().equalsIgnoreCase(code)).findFirst()
				.map(this::offertoResponse).orElseThrow(() -> new ResourceNotFoundException("Offer not found"));
	}

	private void validate(OfferRequest req, boolean creating) {
		if (creating) {
		}
		if (req.getStartDate() != null && req.getEndDate() != null) {
			if (req.getEndDate().isBefore(req.getStartDate()))
				throw new BadRequestException("endDate must be after startDate");
		}
		if (req.getDiscountType() != null && req.getDiscountValue() != null) {
			if (req.getDiscountType().name().equals("PERCENTAGE")
					&& (req.getDiscountValue() < 0 || req.getDiscountValue() > 100)) {
				throw new BadRequestException("Percentage discount must be between 0 and 100");
			}
			if (req.getDiscountType().name().equals("FLAT") && req.getDiscountValue() < 0) {
				throw new BadRequestException("Flat discount must be >= 0");
			}
		}
	}

	private Offer requestToOffer(Offer o, OfferRequest req) {

		o.setCode(req.getCode());
		o.setTitle(req.getTitle());
		o.setDescription(req.getDescription());
		o.setDiscountType(req.getDiscountType());
		o.setDiscountValue(req.getDiscountValue());
		o.setMinOrderAmount(req.getMinOrderAmount());
		o.setMaxDiscountAmount(req.getMaxDiscountAmount());
		o.setStartDate(req.getStartDate());
		o.setEndDate(req.getEndDate());
		return o;
	}

	private OfferResponse offertoResponse(Offer o) {
		OfferResponse r = new OfferResponse();
		r.setId(o.getId());
		r.setCode(o.getCode());
		r.setTitle(o.getTitle());
		r.setDescription(o.getDescription());
		r.setDiscountType(o.getDiscountType());
		r.setDiscountValue(o.getDiscountValue());
		r.setMinOrderAmount(o.getMinOrderAmount());
		r.setMaxDiscountAmount(o.getMaxDiscountAmount());
		r.setStartDate(o.getStartDate());
		r.setEndDate(o.getEndDate());
		r.setActive(o.getActive());
		return r;
	}
}
