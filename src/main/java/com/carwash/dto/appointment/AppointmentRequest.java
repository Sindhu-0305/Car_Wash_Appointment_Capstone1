package com.carwash.dto.appointment;

import java.time.LocalDateTime;

public class AppointmentRequest {

    private Long serviceItemId;
    private Long providerId;
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
