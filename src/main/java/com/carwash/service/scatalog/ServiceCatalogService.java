package com.carwash.service.scatalog;

import java.util.List;

import com.carwash.dto.scatalog.ServiceItemRequest;
import com.carwash.dto.scatalog.ServiceItemResponse;
import com.carwash.enums.scatalog.ServiceCategory;

public interface ServiceCatalogService {

	ServiceItemResponse create(ServiceItemRequest req);

	ServiceItemResponse update(Long id, ServiceItemRequest req);

	void delete(Long id);

	void activate(Long id);

	void deactivate(Long id);

	ServiceItemResponse getById(Long id);

	List<ServiceItemResponse> getAll(boolean onlyActive);

	List<ServiceItemResponse> getByCategory(ServiceCategory category, boolean onlyActive);
}
