package com.carwash.service.scatalog;

import java.util.List;

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

	@Autowired
	private ServiceItemRepository repo;

	@Override
	public ServiceItemResponse create(ServiceItemRequest req) {
		
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
		return toResponse(item);
	}

	@Override
	public ServiceItemResponse update(Long id, ServiceItemRequest req) {
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
			if (repo.existsByCode(req.getCode()))
				throw new ConflictException("Code already exists");
			item.setCode(req.getCode());
		}
		item = repo.save(item);
		return toResponse(item);
	}

	@Override
	public void delete(Long id) {
		repo.deleteById(id);
	}

	@Override
	public void activate(Long id) {
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		item.setActive(true);
		repo.save(item);
	}

	@Override
	public void deactivate(Long id) {
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		item.setActive(false);
		repo.save(item);
	}

	@Override
	public ServiceItemResponse getById(Long id) {
		ServiceItem item = repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("Not found"));
		return toResponse(item);
	}

	@Override
	public List<ServiceItemResponse> getAll(boolean onlyActive) {

		List<ServiceItem> items;
		if (onlyActive) {
			items = repo.findByActiveTrue();
		} else {
			items = repo.findAll();
		}
		return items.stream().map(this::toResponse).toList();

	}

	@Override
	public List<ServiceItemResponse> getByCategory(ServiceCategory category, boolean onlyActive) {

		List<ServiceItem> items;
		if (onlyActive) {
			items = repo.findByCategoryAndActiveTrue(category);
		} else {
			items = repo.findByCategory(category);
		}
		return items.stream().map(this::toResponse).toList();

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
