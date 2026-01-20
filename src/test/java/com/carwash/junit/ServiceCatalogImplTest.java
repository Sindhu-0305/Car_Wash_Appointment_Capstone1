
package com.carwash.junit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.context.SpringBootTest;

import com.carwash.dto.scatalog.ServiceItemRequest;
import com.carwash.dto.scatalog.ServiceItemResponse;
import com.carwash.enums.scatalog.ServiceCategory;
import com.carwash.service.scatalog.ServiceCatalogServiceImpl;

@SpringBootTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
@Transactional
class ServiceCatalogServiceCreateSuccessIT {

    @Autowired
    private ServiceCatalogServiceImpl service;

    @Test
    void test1() {

        ServiceItemRequest req = new ServiceItemRequest();
        req.setCode("CODE1");
        req.setName("Premium Interior");
        req.setDescription("Deep interior detailing");
        req.setPrice(1299.0);
        req.setCategory(ServiceCategory.INTERIOR);
        req.setDurationMinutes(90);

        ServiceItemResponse res = service.create(req);

        assertNotNull(res);
        assertNotNull(res.getId());
        assertEquals("Premium Interior", res.getName());
        assertEquals("Deep interior detailing", res.getDescription());
        assertEquals(1299.0, res.getPrice());
        assertEquals(ServiceCategory.INTERIOR, res.getCategory());
        assertEquals(90, res.getDurationMinutes());
        assertEquals(Boolean.TRUE, res.getActive());
    }
}
