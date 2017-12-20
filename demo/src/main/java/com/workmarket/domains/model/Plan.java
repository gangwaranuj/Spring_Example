package com.workmarket.domains.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.planconfig.AbstractPlanConfig;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Pattern;
import java.util.List;

@Entity(name = "plan")
@Table(name = "plan")
@AuditChanges
@JsonIgnoreProperties({
	"createdOn", "createdOnString", "creatorId", "deleted", "encryptedId",
	"idHash", "modifiedOn", "modifiedOnString", "modifierId"
})
public class Plan extends DeletableEntity {
	private String description;
	private String code;
	private List<AbstractPlanConfig> planConfigs;

	@Column(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Column(name = "code")
	@Pattern(regexp = "^[A-Z0-9]+$")
	public String getCode() {
		return code;
	}

	@OneToMany(cascade = CascadeType.ALL, mappedBy = "plan", fetch = FetchType.EAGER)
	public List<AbstractPlanConfig> getPlanConfigs() {
		return planConfigs;
	}

	public void setPlanConfigs(List<AbstractPlanConfig> planConfigs) {
		this.planConfigs = planConfigs;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
