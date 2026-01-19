package com.carwash.controller.scatalog;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.carwash.dto.scatalog.ServiceItemRequest;
import com.carwash.dto.scatalog.ServiceItemResponse;
import com.carwash.enums.scatalog.ServiceCategory;
import com.carwash.repository.scatalog.ServiceItemRepository;
import com.carwash.service.scatalog.ServiceCatalogService;

@RestController
public class ServiceCatalogController {

	@Autowired
	private ServiceCatalogService service;
	
	@Autowired
	private ServiceItemRepository serviceRepo;


    @GetMapping("/customers/catalog")
    public ResponseEntity<List<ServiceItemResponse>> getAll() {
        return ResponseEntity.ok(service.getAll(true));
    }

    @GetMapping("/customers/catalog/category/{category}")
    public ResponseEntity<List<ServiceItemResponse>> getByCategory(@PathVariable ServiceCategory category) {
        return ResponseEntity.ok(service.getByCategory(category, true));
    }

    @PostMapping("/admin/catalog")
    public ResponseEntity<ServiceItemResponse> create(@RequestBody ServiceItemRequest req) {
        return ResponseEntity.ok(service.create(req));
    }

    @PutMapping("/admin/catalog/{id}")
    public ResponseEntity<ServiceItemResponse> update(@PathVariable Long id, @RequestBody ServiceItemRequest req) {
        return ResponseEntity.ok(service.update(id, req));
    }	

    @PutMapping("/admin/catalog/{id}/activate")
    public ResponseEntity<?> activate(@PathVariable Long id) {
        service.activate(id);
        return ResponseEntity.ok("Activated successfully");    }

    @PutMapping("/admin/catalog/{id}/deactivate")
    public ResponseEntity<?> deactivate(@PathVariable Long id) {
        service.deactivate(id);
        return ResponseEntity.ok("Deactivated successfully");    }

    @DeleteMapping("/admin/catalog/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Deleted successfully");    }

    @GetMapping("/admin/catalog/{id}")
    public ResponseEntity<ServiceItemResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/admin/catalog")
    public ResponseEntity<List<ServiceItemResponse>> getAllAdmin(@RequestParam(defaultValue = "false") boolean onlyActive) {
        return ResponseEntity.ok(service.getAll(onlyActive));
    }

}
