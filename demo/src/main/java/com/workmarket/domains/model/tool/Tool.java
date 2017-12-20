package com.workmarket.domains.model.tool;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.ApprovableVerifiableEntity;
import com.workmarket.domains.model.Industry;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name = "tool")
@Table(name = "tool")
@AuditChanges
public class Tool extends ApprovableVerifiableEntity {

	private static final long serialVersionUID = 1L;

	private String name;
	private String description;
	private Long popularity = 0L;
	private Industry industry = Industry.NONE;

	@Column(name = "name", length = 100, unique = true, nullable = false)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "description", length = 2000, unique = false, nullable = false)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "popularity", nullable = false)
	public Long getPopularity() {
		return popularity;
	}

	public void setPopularity(Long popularity) {
		this.popularity = popularity;
	}

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "industry_id", nullable = false)
	public Industry getIndustry() {
		return industry;
	}

	public void setIndustry(Industry industry) {
		this.industry = industry;
	}
}
