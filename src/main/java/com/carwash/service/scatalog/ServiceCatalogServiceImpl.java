package com.carwash.service.scatalog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.carwash.dto.scatalog.ServiceItemRequest;
import com.carwash.dto.scatalog.ServiceItemResponse;
import com.carwash.entity.scatalog.ServiceItem;
import com.carwash.enums.scatalog.ServiceCategory;
import com.carwash.exception.ConflictException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.scatalog.ServiceItemRepository;

@Service
public class ServiceCatalogServiceImpl implements ServiceCatalogService {
	private static final Logger log = LoggerFactory.getLogger(ServiceCatalogServiceImpl.class);
	@Autowired
	private ServiceItemRepository repo;

	@Override
	public ServiceItemResponse create(ServiceItemRequest req) {
		log.info("Creating catalog item code={}", req.getCode());
		if (repo.existsByCode(req.getCode())) {
			throw new ConflictException("Code already exists");
		}
		ServiceItem item = new ServiceItem();
		item.setCode(req.getCode());
		item.setName(req.getName());
		item.setDescription(req.getDescription());
		item.setPrice(req.getPrice());
		item.setCategory(req.getCategory());
		item.setDurationMinutes(req.getDurationMinutes());
		item.setActive(true);
		item = repo.save(item);
		log.info("Catalog item created id={}", item.getId());
		return toResponse(item);
	}

	@Override
	public ServiceItemResponse update(Long id, ServiceItemRequest req) {
		log.info("Updating catalog item id={}", id);
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		if (req.getName() != null)
			item.setName(req.getName());
		if (req.getDescription() != null)
			item.setDescription(req.getDescription());
		if (req.getPrice() != null)
			item.setPrice(req.getPrice());
		if (req.getCategory() != null)
			item.setCategory(req.getCategory());
		if (req.getDurationMinutes() != null)
			item.setDurationMinutes(req.getDurationMinutes());
		if (req.getCode() != null && !req.getCode().equals(item.getCode())) {
			if (repo.existsByCode(req.getCode())) {
				log.warn("Catalog update failed — code already exists {}", req.getCode());
				throw new ConflictException("Code already exists");
			}
			item.setCode(req.getCode());
		}
		item = repo.save(item);
		log.info("Catalog item updated id={}", id);
		return toResponse(item);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);

		log.info("Catalog item deleted id={}", id);

	}

	@Override
	public void activate(Long id) {
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		item.setActive(true);
		repo.save(item);
		log.info("Catalog item activated id={}", id);
	}

	@Override
	public void deactivate(Long id) {
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		item.setActive(false);
		repo.save(item);
		log.info("Catalog item deactivated id={}", id);
	}

	@Override
	public ServiceItemResponse getById(Long id) {
		log.info("Fetching catalog item id={}", id);
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		return toResponse(item);
	}

	@Override
	public List<ServiceItemResponse> getAll(boolean onlyActive) {
		log.info("Fetching catalog items onlyActive={}", onlyActive);
		List<ServiceItem> items;
		if (onlyActive) {
			items = repo.findByActiveTrue();
		} else {
			items = repo.findAll();
		}
		List<ServiceItemResponse> result = new ArrayList<>();
		for (ServiceItem i : items) {
			result.add(toResponse(i));
		}
		return result;
	}

	@Override
	public List<ServiceItemResponse> getByCategory(ServiceCategory category, boolean onlyActive) {
		log.info("Fetching catalog items by category={} onlyActive={}", category, onlyActive);
		List<ServiceItem> items;
		if (onlyActive) {
			items = repo.findByCategoryAndActiveTrue(category);
		} else {
			items = repo.findByCategory(category);
		}
		log.info("Fetched {} catalog items for category={}", items.size(), category);

		List<ServiceItemResponse> result = new ArrayList<>();
		for (ServiceItem i : items) {
			result.add(toResponse(i));
		}
		return result;

	}

	private ServiceItemResponse toResponse(ServiceItem item) {
		ServiceItemResponse res = new ServiceItemResponse();
		res.setId(item.getId());
		res.setCode(item.getCode());
		res.setName(item.getName());
		res.setDescription(item.getDescription());
		res.setPrice(item.getPrice());
		res.setCategory(item.getCategory());
		res.setDurationMinutes(item.getDurationMinutes());
		res.setActive(item.getActive());
		return res;
	}

}
