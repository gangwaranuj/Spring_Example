package com.workmarket.service.business.dto;


public class TagDTO {

	private Long tagId;
	private String name;

	public TagDTO() {
	}

	public TagDTO(String name) {
		this.name = name;
	}

	public Long getTagId() {
		return tagId;
	}

	public void setTagId(Long tagId) {
		this.tagId = tagId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
