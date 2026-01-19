package com.carwash.dto.scatalog;

import com.carwash.enums.scatalog.ServiceCategory;

public class ServiceItemResponse {
    private Long id;
    private String code;
    private String name;
    private String description;
    private Double price;
    private ServiceCategory category;
    private Integer durationMinutes;
    private Boolean active;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
	public ServiceCategory getCategory() {
		return category;
	}
	public void setCategory(ServiceCategory category) {
		this.category = category;
	}
	public Integer getDurationMinutes() {
		return durationMinutes;
	}
	public void setDurationMinutes(Integer durationMinutes) {
		this.durationMinutes = durationMinutes;
	}
	public Boolean getActive() {
		return active;
	}
	public void setActive(Boolean active) {
		this.active = active;
	}
    
    
}