package com.workmarket.domains.model.account.payment;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.StringUtilities;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Created by nick on 9/17/12 5:41 PM
 */
@Entity(name = "paymentTermsDuration")
@Table(name = "payment_terms_duration")
@AuditChanges
public class PaymentTermsDuration extends DeletableEntity {

	public static final String SYSTEM_NOT_DELETABLE = "system_not_deletable";
	public static final String SYSTEM = "system";
	public static final String CUSTOM = "custom";

	Integer numDays;
	String type;

	public PaymentTermsDuration() {
	}

	public PaymentTermsDuration(Integer numDays, String type) {
		this.numDays = numDays;
		this.type = type;
	}

	@Column(name = "num_days", nullable = false)
	public Integer getNumDays() {
		return numDays;
	}

	public void setNumDays(Integer numDays) {
		this.numDays = numDays;
	}

	@Column(name = "type", nullable = false)
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Transient
	public boolean isDeletable() {
		return SYSTEM_NOT_DELETABLE.equals(type);
	}

	@Transient
	public boolean isSystemDuration() {
		return StringUtilities.equalsAny(type, SYSTEM, SYSTEM_NOT_DELETABLE);
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}


}
