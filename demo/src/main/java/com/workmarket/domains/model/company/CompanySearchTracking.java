package com.workmarket.domains.model.company;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Set;

/**
 * author: rocio
 */
@Entity(name="companySearchTracking")
@Table(name="company_search_tracking")
@AuditChanges
public class CompanySearchTracking extends DeletableEntity {

	private static final long serialVersionUID = 7033135931766151699L;

	private Company trackingCompany;
	private Set<Long> trackedCompanyIds;
	private int emailOnNumberOfResults;
	private String emailTo;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getTrackingCompany() {
		return trackingCompany;
	}

	public void setTrackingCompany(Company trackingCompany) {
		this.trackingCompany = trackingCompany;
	}

	@ElementCollection
	@CollectionTable(name = "company_search_tracked_companies",
			joinColumns = @JoinColumn(name = "company_search_tracking_id"))
	@Column(name = "tracked_company_id")
	public Set<Long> getTrackedCompanyIds() {
		return trackedCompanyIds;
	}

	public void setTrackedCompanyIds(Set<Long> trackedCompanyIds) {
		this.trackedCompanyIds = trackedCompanyIds;
	}

	@Column(name = "email_on_results")
	public int getEmailOnNumberOfResults() {
		return emailOnNumberOfResults;
	}

	public void setEmailOnNumberOfResults(int emailOnNumberOfResults) {
		this.emailOnNumberOfResults = emailOnNumberOfResults;
	}

	@Column(name = "email_to", nullable = false)
	public String getEmailTo() {
		return emailTo;
	}

	public void setEmailTo(String emailTo) {
		this.emailTo = emailTo;
	}

	@Override
	public String toString() {
		return "CompanySearchTracking{" +
				"trackingCompany=" + trackingCompany +
				", trackedCompanyIds=" + trackedCompanyIds +
				", emailOnNumberOfResults=" + emailOnNumberOfResults +
				", emailTo='" + emailTo + '\'' +
				'}';
	}
}
