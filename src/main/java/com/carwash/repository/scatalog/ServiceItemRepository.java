package com.carwash.repository.scatalog;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carwash.entity.scatalog.ServiceItem;
import com.carwash.enums.scatalog.ServiceCategory;

@Repository
public interface ServiceItemRepository extends JpaRepository<ServiceItem, Long> {

	boolean existsByCode(String code);
    List<ServiceItem> findByActiveTrue();
    List<ServiceItem> findByCategory(ServiceCategory category);
    List<ServiceItem> findByCategoryAndActiveTrue(ServiceCategory category);
	
}
