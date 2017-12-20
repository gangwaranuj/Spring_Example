package com.workmarket.domains.compliance.model;

import org.apache.commons.lang.builder.HashCodeBuilder;

public class ComplianceRuleType {
	private final String name;
	private final String humanName;
	private final boolean allowMultiple;

	public ComplianceRuleType(String name, String humanName, boolean allowMultiple) {
		this.name = name;
		this.humanName = humanName;
		this.allowMultiple = allowMultiple;
	}

	public String getName() {
		return name;
	}

	public String getHumanName() {
		return humanName;
	}

	public boolean isAllowMultiple() { return allowMultiple; }

	@Override
	public boolean equals(Object obj) {
		if (obj == null || obj.getClass() != getClass()) { return false; }
		if (obj == this) { return true; }

		ComplianceRuleType that = (ComplianceRuleType) obj;

		boolean nameMatch = (
			(this.name == null && that.getName() == null) ||
			(this.name != null && this.name.equals(that.getName()))
		);

		boolean humanNameMatch = (
			(this.humanName == null && that.getHumanName() == null) ||
			(this.humanName != null && this.humanName.equals(that.getHumanName()))
		);

		boolean allowMultipleMatch = (
			this.isAllowMultiple() == that.isAllowMultiple()
		);

		return (nameMatch && humanNameMatch && allowMultipleMatch);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

}
