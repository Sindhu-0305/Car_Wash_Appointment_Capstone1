package com.carwash.dto.offer;

import java.time.LocalDate;

import com.carwash.enums.offer.DiscountType;

public class OfferRequest {

	private String code;
	private String title;
	private String description;
	private DiscountType discountType;
	private Double discountValue;
	private Double minOrderAmount;
	private Double maxDiscountAmount;
	private LocalDate startDate;
	private LocalDate endDate;
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public DiscountType getDiscountType() {
		return discountType;
	}
	public void setDiscountType(DiscountType discountType) {
		this.discountType = discountType;
	}
	public Double getDiscountValue() {
		return discountValue;
	}
	public void setDiscountValue(Double discountValue) {
		this.discountValue = discountValue;
	}
	public Double getMinOrderAmount() {
		return minOrderAmount;
	}
	public void setMinOrderAmount(Double minOrderAmount) {
		this.minOrderAmount = minOrderAmount;
	}
	public Double getMaxDiscountAmount() {
		return maxDiscountAmount;
	}
	public void setMaxDiscountAmount(Double maxDiscountAmount) {
		this.maxDiscountAmount = maxDiscountAmount;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}
	public LocalDate getEndDate() {
		return endDate;
	}
	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
}
