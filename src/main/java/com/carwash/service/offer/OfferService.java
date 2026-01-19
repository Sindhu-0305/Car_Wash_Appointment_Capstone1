package com.carwash.service.offer;

import java.util.List;

import org.springframework.stereotype.Service;

import com.carwash.dto.offer.OfferRequest;
import com.carwash.dto.offer.OfferResponse;

@Service
public interface OfferService {

	OfferResponse create(OfferRequest req);

	OfferResponse update(Long id, OfferRequest req);

	void delete(Long id);

	void activate(Long id);

	void deactivate(Long id);

	OfferResponse getById(Long id);

	List<OfferResponse> getAll(boolean onlyActive, boolean onlyRunningToday);

	OfferResponse getByCode(String code);
}
