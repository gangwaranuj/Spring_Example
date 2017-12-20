package com.workmarket.service.business.dto;

public class SpecialtyDTO {

    private Long specialtyId;
	private String name;
	private String description;
	private Long industryId;

    public Long getSpecialtyId() {
        return specialtyId;
    }

    public void setSpecialtyId(Long specialtyId) {
        this.specialtyId = specialtyId;
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

	public void setIndustryId(Long industryId) {
		this.industryId = industryId;
	}

	public Long getIndustryId() {
		return industryId;
	}
}