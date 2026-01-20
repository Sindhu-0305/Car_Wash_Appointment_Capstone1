
package com.carwash.junit;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import com.carwash.dto.offer.OfferRequest;
import com.carwash.enums.offer.DiscountType;
import com.carwash.exception.ConflictException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.service.offer.OfferServiceImpl;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
class OfferServiceImplIT {

    @Autowired
    private OfferServiceImpl service;

    @Test
    void createOffer_duplicateCode_shouldThrowConflict() {
        String code = "OFR_" + System.nanoTime();

        OfferRequest first = new OfferRequest();
        first.setCode(code);
        first.setTitle("New Year 50");
        first.setDescription("Flat 50% off");
        first.setDiscountType(DiscountType.PERCENTAGE);
        first.setDiscountValue(50.0);
        first.setMinOrderAmount(400.0);
        first.setMaxDiscountAmount(300.0);
        first.setStartDate(LocalDate.now().minusDays(1));
        first.setEndDate(LocalDate.now().plusDays(10));
        service.create(first);

        OfferRequest dup = new OfferRequest();
        dup.setCode(code);
        dup.setTitle("Duplicate");
        dup.setDescription("Duplicate");
        dup.setDiscountType(DiscountType.PERCENTAGE);
        dup.setDiscountValue(50.0);
        dup.setMinOrderAmount(400.0);
        dup.setMaxDiscountAmount(300.0);
        dup.setStartDate(LocalDate.now().minusDays(1));
        dup.setEndDate(LocalDate.now().plusDays(10));

        assertThrows(ConflictException.class, () -> service.create(dup));
    }

    @Test
    void getById_notFound_shouldThrowNotFound() {
        assertThrows(ResourceNotFoundException.class, () -> service.getById(999_999L));
    }
}
