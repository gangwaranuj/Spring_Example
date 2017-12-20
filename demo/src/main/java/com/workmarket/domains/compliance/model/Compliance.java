package com.workmarket.domains.compliance.model;

import java.util.Set;

public class Compliance {
	private final Set<BaseComplianceCriterion> complianceCriteria;
	private boolean compliant;

	public Compliance(Set<BaseComplianceCriterion> complianceCriteria, boolean compliant) {
		this.complianceCriteria = complianceCriteria;
		this.compliant = compliant;
	}

	public Set<BaseComplianceCriterion> getComplianceCriteria() {
		return complianceCriteria;
	}

	public boolean isCompliant() {
		return compliant;
	}
}

