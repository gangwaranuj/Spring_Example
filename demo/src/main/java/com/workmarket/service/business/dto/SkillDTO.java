package com.workmarket.service.business.dto;

public class SkillDTO {

	private Long skillId;
	private String name;
	private String description;
	private Long industryId;
	
	public SkillDTO() {}
	public SkillDTO(Long skillId) {
		this.skillId = skillId;
	}
	public SkillDTO(String name) {
		this.name = name;
	}
	public SkillDTO(Long skillId, String name) {
		this.skillId = skillId;
		this.name = name;
	}

	public Long getSkillId() {
		return skillId;
	}

	public void setSkillId(Long skillId) {
		this.skillId = skillId;
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