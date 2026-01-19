package com.carwash.entity.appointment;

import java.time.LocalDateTime;

import com.carwash.entity.ServiceProvider;
import com.carwash.entity.User;
import com.carwash.entity.scatalog.ServiceItem;
import com.carwash.enums.appointment.BookingStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;


@Entity
@Table(name="appointments")
public class Appointment {

	 @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Long id;

	    @ManyToOne(optional = false)
	    private User customer;

	    @ManyToOne(optional = false)
	    private ServiceProvider serviceProvider;

	    @ManyToOne(optional = false)
	    private ServiceItem serviceItem;

	    @Column(nullable = false)
	    private LocalDateTime scheduledAt;

	    @Column(nullable = false)
	    private Integer durationMinutes;

	    @Column(nullable = false)
	    private Double price;

	    @Enumerated(EnumType.STRING)
	    @Column(nullable = false, length = 20)
	    private BookingStatus status = BookingStatus.PENDING;

	    private String notes;

		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public User getCustomer() {
			return customer;
		}

		public void setCustomer(User customer) {
			this.customer = customer;
		}

		public ServiceProvider getServiceProvider() {
			return serviceProvider;
		}

		public void setServiceProvider(ServiceProvider serviceProvider) {
			this.serviceProvider = serviceProvider;
		}

		public ServiceItem getServiceItem() {
			return serviceItem;
		}

		public void setServiceItem(ServiceItem serviceItem) {
			this.serviceItem = serviceItem;
		}

		public LocalDateTime getScheduledAt() {
			return scheduledAt;
		}

		public void setScheduledAt(LocalDateTime scheduledAt) {
			this.scheduledAt = scheduledAt;
		}

		public Integer getDurationMinutes() {
			return durationMinutes;
		}

		public void setDurationMinutes(Integer durationMinutes) {
			this.durationMinutes = durationMinutes;
		}

		public Double getPrice() {
			return price;
		}

		public void setPrice(Double price) {
			this.price = price;
		}

		public BookingStatus getStatus() {
			return status;
		}

		public void setStatus(BookingStatus status) {
			this.status = status;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}
	    
	    
}
