package com.carwash.repository.offer;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.carwash.entity.offer.Offer;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

	boolean existsByCode(String code);
    List<Offer> findByActiveTrue();
    List<Offer> findByActiveTrueAndStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate start, LocalDate end);

}
