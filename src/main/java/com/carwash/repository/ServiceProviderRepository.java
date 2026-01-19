package com.carwash.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carwash.entity.ServiceProvider;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, Long> {

//	List<ServiceProvider> findBySpecialization(String specialization);
//	List<ServiceProvider> fingByExpGreaterThanEqual(Integer years);
	
}
