package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name="language")
@Table(name="language")
public class Language extends AbstractEntity {
	
	private String description;

	@Column(name = "description", nullable = false, length=45)	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
}