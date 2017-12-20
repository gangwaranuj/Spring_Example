package com.workmarket.domains.forums.model;

import com.workmarket.domains.model.AbstractEntity;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;

@Entity(name = "forum_category")
@Access(AccessType.PROPERTY)
public class ForumCategory extends AbstractEntity {

	private String categoryName;
	private String description;

	@Column(name="category_name", nullable = false)
	public String getCategoryName() {
		return this.categoryName;
	}

	public void setCategoryName(String categoryName){
		this.categoryName =  categoryName;
	}

	@Column(name="description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
