package com.carwash.junit;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.carwash.dto.offer.OfferRequest;
import com.carwash.exception.ConflictException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.offer.OfferRepository;
import com.carwash.service.offer.OfferServiceImpl;

class OfferServiceImplTest {

    @InjectMocks
    private OfferServiceImpl service;

    @Mock
    private OfferRepository offerRepo;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    
    @Test
    void testCreateOffer_DuplicateCode() {
        OfferRequest req = new OfferRequest();
        req.setCode("NEW50");

        when(offerRepo.existsByCode("NEW50")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(req));
    }

    
    @Test
    void testGetById_NotFound() {
        when(offerRepo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(999L));
    }
}

