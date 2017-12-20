package com.workmarket.domains.model.requirementset;

import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.domains.work.model.AbstractWork;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "requirement_set")
@AuditChanges
public class RequirementSet extends AuditedEntity {
	private String name;
	private boolean required;
	private boolean active;
	private List<AbstractRequirement> requirements = new ArrayList<>();
	private Company company;
	private Set<AbstractWork> work;
	private String creatorFullName;
	private UserGroup userGroup;

	@NotBlank
	@Column(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Column(name = "required")
	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}

	@Column(name = "active")
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	@JoinColumn(name = "requirement_set_id")
	public List<AbstractRequirement> getRequirements() {
		return requirements;
	}

	public void setRequirements(List<AbstractRequirement> requirements) {
		this.requirements = requirements;
	}

	@ManyToOne
	@JoinColumn(name = "company_id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public void setCompany(Company company) {
		this.company = company;
	}

	@ManyToMany
	@JoinTable(name = "work_requirement_set_association",
		joinColumns = @JoinColumn(name = "requirement_set_id"),
		inverseJoinColumns = @JoinColumn(name = "work_id"))
	public Set<AbstractWork> getWork() {
		return work;
	}
	public void setWork(Set<AbstractWork> work) {
		this.work = work;
	}

	@OneToOne
	@JoinTable(name = "user_group_requirement_set_association",
		joinColumns = @JoinColumn(name = "requirement_set_id"),
		inverseJoinColumns = @JoinColumn(name = "user_group_id"))
	public UserGroup getUserGroup() {
		return userGroup;
	}
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}

	@Transient
	public String getCreatorFullName() {
		return creatorFullName;
	}

	public void setCreatorFullName(String creatorFullName) {
		this.creatorFullName = creatorFullName;
	}
}
