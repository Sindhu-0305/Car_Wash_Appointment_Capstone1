package com.carwash.controller.offer;

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

import com.carwash.dto.offer.OfferRequest;
import com.carwash.dto.offer.OfferResponse;
import com.carwash.repository.offer.OfferRepository;
import com.carwash.service.offer.OfferService;

@RestController
public class OfferController {

	@Autowired
	private OfferService offerService;
	
	@Autowired
	private OfferRepository offerRepo;
	
	 @GetMapping("/customers/offers")
	    public ResponseEntity<List<OfferResponse>> getActiveOffers(@RequestParam(defaultValue = "true") boolean runningToday) {
	        return ResponseEntity.ok(offerService.getAll(true, runningToday));
	    }

	    @GetMapping("/customers/offers/{code}")
	    public ResponseEntity<OfferResponse> getByCode(@PathVariable String code) {
	        return ResponseEntity.ok(offerService.getByCode(code));
	    }

	    @PostMapping("/admin/offers")
	    public ResponseEntity<OfferResponse> create(@RequestBody OfferRequest req) {
	        return ResponseEntity.ok(offerService.create(req));
	    }

	    @PutMapping("/admin/offers/{id}")
	    public ResponseEntity<OfferResponse> update(@PathVariable Long id, @RequestBody OfferRequest req) {
	        return ResponseEntity.ok(offerService.update(id, req));
	    }

	    @PutMapping("/admin/offers/{id}/activate")
	    public ResponseEntity<?> activate(@PathVariable Long id) {
	    	offerService.activate(id);
	        return ResponseEntity.ok("Offer activated");
	    }

	    @PutMapping("/admin/offers/{id}/deactivate")
	    public ResponseEntity<?> deactivate(@PathVariable Long id) {
	    	offerService.deactivate(id);
	        return ResponseEntity.ok("Offer deactivated");
	    }

	    @DeleteMapping("/admin/offers/{id}")
	    public ResponseEntity<?> delete(@PathVariable Long id) {
	    	offerService.delete(id);
	        return ResponseEntity.ok("Offer deleted");
	    }

	    @GetMapping("/admin/offers/{id}")
	    public ResponseEntity<OfferResponse> getById(@PathVariable Long id) {
	        return ResponseEntity.ok(offerService.getById(id));
	    }

	    @GetMapping("/admin/offers")
	    public ResponseEntity<List<OfferResponse>> getAllAdmin(@RequestParam(defaultValue = "false") boolean onlyActive,
	                                                           @RequestParam(defaultValue = "false") boolean runningToday) {
	        return ResponseEntity.ok(offerService.getAll(onlyActive, runningToday));
	    }

}
