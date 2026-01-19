package com.carwash.junit;



import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.carwash.dto.scatalog.ServiceItemRequest;
import com.carwash.exception.ConflictException;
import com.carwash.exception.ResourceNotFoundException;
import com.carwash.repository.scatalog.ServiceItemRepository;
import com.carwash.service.scatalog.ServiceCatalogServiceImpl;

class ServiceCatalogServiceImplTest {

    @InjectMocks
    private ServiceCatalogServiceImpl service;

    @Mock
    private ServiceItemRepository repo;

    @BeforeEach
    void init() {
        MockitoAnnotations.openMocks(this);
    }

    
  
    @Test
    void testCreate_DuplicateCode() {
        ServiceItemRequest req = new ServiceItemRequest();
        req.setCode("WASH001");

        when(repo.existsByCode("WASH001")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(req));
    }

  
    @Test
    void testGetById_NotFound() {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getById(999L));
    }
}
