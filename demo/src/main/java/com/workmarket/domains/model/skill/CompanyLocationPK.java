package com.workmarket.domains.model.skill;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
@Access(AccessType.PROPERTY)
class CompanyLocationPK implements Serializable {
	private Long companyId;
	private Long locationId;

	public CompanyLocationPK() {

	}

	public CompanyLocationPK(Long companyId, Long locationId) {
		this.companyId = companyId;
		this.locationId = locationId;
	}

	@Column(name = "company_id", nullable = false)
	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;
	}

	@Column(name = "location_id", nullable = false)
	public Long getLocationId() {
		return locationId;
	}

	public void setLocationId(Long locationId) {
		this.locationId = locationId;
	}
}
