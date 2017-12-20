package com.workmarket.domains.model.requirementset.country;

import com.workmarket.domains.model.requirementset.Requirable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "country")
public class CountryRequirable implements Requirable {

	private static final long serialVersionUID = -6051994310063201188L;

	private String id;
	private String name;

	@Id
	@Column(
		name = "id",
		length = 3,
		nullable = false,
		insertable = false,
		updatable = false)
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	@Column(name="name", insertable = false, updatable = false)
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
}
