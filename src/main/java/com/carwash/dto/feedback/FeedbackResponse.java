package com.carwash.dto.feedback;

import java.time.LocalDateTime;

public class FeedbackResponse {

	private Long id;
	private Long appointmentId;
	private Long serviceProviderId;
	private Long customerId;
	private Integer rating;
	private String comment;
	private LocalDateTime createdAt;

	

	public FeedbackResponse(Long id, Long appointmentId, Long serviceProviderId, Long customerId, Integer rating,
			String comment, LocalDateTime createdAt) {
		super();
		this.id = id;
		this.appointmentId = appointmentId;
		this.serviceProviderId = serviceProviderId;
		this.customerId = customerId;
		this.rating = rating;
		this.comment = comment;
		this.createdAt = createdAt;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getAppointmentId() {
		return appointmentId;
	}

	public void setAppointmentId(Long appointmentId) {
		this.appointmentId = appointmentId;
	}

	public Long getServiceProviderId() {
		return serviceProviderId;
	}

	public void setServiceProviderId(Long serviceProviderId) {
		this.serviceProviderId = serviceProviderId;
	}

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
