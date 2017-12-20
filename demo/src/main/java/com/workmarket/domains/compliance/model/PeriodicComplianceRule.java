package com.workmarket.domains.compliance.model;

import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

@MappedSuperclass
public abstract class PeriodicComplianceRule extends AbstractComplianceRule {
	public enum PeriodType {
		WEEK("weekly"),
		MONTH("monthly"),
		QUARTER("quarterly"),
		YEAR("yearly"),
		LIFETIME("lifetime");

		private String column;

		PeriodType(String column) { this.column = column; }

		public String getColumn() {
			return column;
		}
	}

	private PeriodType periodType;
	private Long periodValue;
	private Boolean repeat;

	@Column(name = "period_type")
	@Enumerated(EnumType.STRING)
	public PeriodType getPeriodType() { return periodType; }

	public void setPeriodType(PeriodType periodType) { this.periodType = periodType; }

	@Column(name = "period_value")
	public Long getPeriodValue() { return periodValue; }

	public void setPeriodValue(Long periodValue) { this.periodValue = periodValue; }

	@Column(name = "is_repeat")
	public Boolean isRepeat() {
		return repeat;
	}

	public void setRepeat(Boolean repeat) {
		this.repeat = repeat;
	}

	// These static methods are used by ComplianceRuleTypeDAO

	// Any PeriodicComplianceRule subclass will allowMultiple,
	// so there's no need to override this in subclasses.
	@Transient
	public static boolean allowMultiple() { return true; }

	@Transient
	public static String getHumanTypeName() { return "PeriodicComplianceRule should not be seen by humans"; }
}
