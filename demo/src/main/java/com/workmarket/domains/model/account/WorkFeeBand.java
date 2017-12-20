package com.workmarket.domains.model.account;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.audit.AuditChanges;

@AuditChanges
@Entity(name = "workFeeBand")
@Table(name = "work_fee_band")
public class WorkFeeBand extends AbstractEntity {

	private static final long serialVersionUID = 1L;
	public static final BigDecimal MAXIMUM = BigDecimal.valueOf(100000000L);

	private WorkFeeConfiguration workFeeConfiguration;
	private BigDecimal minimum;
	private BigDecimal maximum;
	private BigDecimal percentage;
	private transient Integer level = 1;//Default level, to be assigned.

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "work_fee_configuration_id", referencedColumnName = "id")
	public WorkFeeConfiguration getWorkFeeConfiguration() {
		return workFeeConfiguration;
	}

	@Column(name = "minimum")
	public BigDecimal getMinimum() {
		return minimum;
	}

	@Column(name = "maximum")
	public BigDecimal getMaximum() {
		return maximum;
	}

	@Column(name = "percentage")
	public BigDecimal getPercentage() {
		return percentage;
	}


	public void setMinimum(BigDecimal minimum) {
		this.minimum = minimum;
	}

	public void setMaximum(BigDecimal maximum) {
		this.maximum = maximum;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public void setWorkFeeConfiguration(WorkFeeConfiguration workFeeConfiguration) {
		this.workFeeConfiguration = workFeeConfiguration;
	}

	/**
	 * @return the level
	 */
	@Transient
	public Integer getLevel() {
		return level;
	}

	/**
	 * @param level the level to set
	 */
	public void setLevel(Integer level) {
		this.level = level;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder("WorkFeeBand[");
		sb.append("minimum:" + minimum);
		sb.append(", maximum:" + maximum);
		sb.append(", percentage:" + percentage);
		sb.append(", level:" + level);
		sb.append("]");
		return sb.toString();
	}

}
