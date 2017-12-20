package com.workmarket.domains.model.planconfig;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.workmarket.domains.model.Plan;
import com.workmarket.domains.model.audit.AuditedEntity;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.beans.Transient;

/**
 * User: micah
 * Date: 8/27/14
 * Time: 6:56 PM
 */
@Entity
@Table(name = "plan_config")
@Inheritance(strategy = InheritanceType.JOINED)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
	@JsonSubTypes.Type(value = TransactionFeePlanConfig.class, name = "transactionFee")
})
@JsonIgnoreProperties({
	"createdOn", "createdOnString", "creatorId", "deleted", "encryptedId",
	"idHash", "modifiedOn", "modifiedOnString", "modifierId", "plan"
})
public abstract class AbstractPlanConfig extends AuditedEntity {
	private Plan plan;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "plan_id")
	public Plan getPlan() { return plan; }
	public void setPlan(Plan plan) { this.plan = plan; }

	@Transient
	public abstract void accept(PlanConfigVisitor visitor, Long companyId);
}
