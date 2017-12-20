package com.workmarket.domains.velvetrope.model;

import com.workmarket.velvetrope.Venue;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

public class BetaFeatureAdmissionRequest {

	@NotNull
	private Venue betaFeature;

	@NotNull
	private List<Long> companyIds;

	public Venue getBetaFeature() {
		return betaFeature;
	}

	public void setBetaFeature(Venue betaFeature) {
		this.betaFeature = betaFeature;
	}

	public List<Long> getCompanyIds() {
		return companyIds;
	}

	public void setCompanyIds(List<Long> companyIds) {
		this.companyIds = companyIds;
	}
}
