package com.workmarket.domains.model.requirementset;

import java.util.Set;

public class Eligibility {
	private final Set<Criterion> criteria;
	private boolean eligible;

	public Eligibility(Set<Criterion> criteria, boolean eligible) {
		this.criteria = criteria;
		this.eligible = eligible;
	}

	public Set<Criterion> getCriteria() {
		return criteria;
	}

	public boolean isEligible() {
		return eligible;
	}
}
