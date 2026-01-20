package com.carwash.dto.appointment;

import java.time.LocalDateTime;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;

public class AppointmentRequest {


    @NotNull(message = "Service item ID is required")

    private Long serviceItemId;
    

    @NotNull(message = "Provider ID is required")

    private Long providerId;
    
    @Future(message = "Appointment time must be in the future")
    private LocalDateTime scheduledAt;
    private String notes;
	public Long getServiceItemId() {
		return serviceItemId;
	}
	public void setServiceItemId(Long serviceItemId) {
		this.serviceItemId = serviceItemId;
	}
	public Long getProviderId() {
		return providerId;
	}
	public void setProviderId(Long providerId) {
		this.providerId = providerId;
	}
	public LocalDateTime getScheduledAt() {
		return scheduledAt;
	}
	public void setScheduledAt(LocalDateTime scheduledAt) {
		this.scheduledAt = scheduledAt;
	}
	public String getNotes() {
		return notes;
	}
	public void setNotes(String notes) {
		this.notes = notes;
	}
    
    
}
