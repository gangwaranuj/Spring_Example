package com.workmarket.domains.model.planconfig;

import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * User: micah
 * Date: 8/27/14
 * Time: 7:01 PM
 */
@Entity
@AuditChanges
@Table(name = "transaction_fee_plan_config")
public class TransactionFeePlanConfig extends AbstractPlanConfig {
	@Column(name = "percentage")
	private BigDecimal percentage;

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	@Override
	public void accept(PlanConfigVisitor visitor, Long companyId) {
		visitor.visit(this, companyId);
	}
}
