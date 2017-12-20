package com.workmarket.domains.work.model;

import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import com.google.common.collect.Sets;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@Entity(name="workUploadMappingGroup")
@Table(name="work_upload_mapping_group")
@AuditChanges
public class WorkUploadMappingGroup extends DeletableEntity {
	private static final long serialVersionUID = 1L;

	private String name;
	private Company company;
	private Set<WorkUploadMapping> mappings = Sets.newHashSet();

	@Column(name="name")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	@Fetch(FetchMode.JOIN)
	@ManyToOne
	@JoinColumn(name = "company_id", nullable = false, updatable = false)
	public Company getCompany() {
		return company;
	}
	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToMany(mappedBy = "mappingGroup", fetch = FetchType.LAZY)
	public Set<WorkUploadMapping> getMappings() {
		return mappings;
	}
	public void setMappings(Set<WorkUploadMapping> mappings) {
		this.mappings = mappings;
	}
}
