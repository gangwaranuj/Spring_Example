package com.workmarket.domains.model.reporting;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import org.hibernate.annotations.Where;

@Entity(name = "reportingCriteria")
@Table(name = "reporting_criteria")
@NamedQueries({

		//Find Reporting Criteria by id ("reportKey")
		@NamedQuery(
				name = "reportingCriteria.byReportKey",
				query = "from reportingCriteria rc where rc.id = :reportKey AND deleted = 0"),

		//Find Reporting Criteria by companyId
		@NamedQuery(
				name = "reportingCriteria.byCompanyId",
				query = "from reportingCriteria rc where rc.company = :companyId AND deleted = 0"),

		//Find all Reporting Criteria
		@NamedQuery(
				name = "reportingCriteria.selectAll",
				query = "from reportingCriteria rc where rc.id > 0 AND deleted = 0"),

		//Find all Reporting Criteria with a list of ids
		@NamedQuery(
				name = "reportingCriteria.selectByIdsList",
				query = "from reportingCriteria rc where rc.id in (:ids) AND deleted = 0"),

		//Find all Reporting Criteria ids
		@NamedQuery(
				name = "reportingCriteria.select_all_ids",
				query = "select id from reportingCriteria rc where rc.id > 0 AND deleted = 0")
})

@AuditChanges
public class ReportingCriteria extends DeletableEntity {

	private Company company;
    private boolean customFieldsReport;
	private String displayKeys;
	private String reportName;
	private List<ReportingCriteriaFiltering> reportingCriteriaFiltering;

	private static final long serialVersionUID = -7740683706605127406L;


	public ReportingCriteria() {
		super();
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "company_id", referencedColumnName = "id")
	public Company getCompany() {
		return company;
	}


	public void setCompany(Company company) {
		this.company = company;
	}

	@OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinColumn(name = "reporting_criteria_id", referencedColumnName = "id")
	@Where(clause = "deleted = 0")
	public List<ReportingCriteriaFiltering> getReportingCriteriaFiltering() {
		return reportingCriteriaFiltering;
	}


	/**
	 * @param reportingCriteriaFiltering the reportingCriteriaFiltering to set
	 */
	public void setReportingCriteriaFiltering(List<ReportingCriteriaFiltering> reportingCriteriaFiltering) {
		this.reportingCriteriaFiltering = reportingCriteriaFiltering;
	}

	/**
	 * @return the displayKeys
	 */
	@Column(name = "display_keys", nullable = true)
	public String getDisplayKeys() {
		return displayKeys;
	}

	/**
	 * @param displayKeys the displayKeys to set
	 */
	public void setDisplayKeys(String displayKeys) {
		this.displayKeys = displayKeys;
	}

	/**
	 * @return the reportName
	 */
	@Column(name = "report_name", nullable = true)
	public String getReportName() {
		return reportName;
	}

	/**
	 * @param reportName the reportName to set
	 */
	public void setReportName(String reportName) {
		this.reportName = reportName;
	}

    @Column(name = "has_custom_fields",nullable=false)
    public boolean isCustomFieldsReport() {
        return customFieldsReport;
    }

    public void setCustomFieldsReport(boolean customFields) {
        this.customFieldsReport = customFields;
    }

    public String toString() {
		StringBuilder sb = new StringBuilder("ReportingCriteria[");
		sb.append("id:" + getId());
		sb.append(", companyId:" + getCompany().getId());
		sb.append(", displayKeys:" + getDisplayKeys());
		sb.append(", reportName:" + getReportName());
        sb.append(", custom fields:" + isCustomFieldsReport());
		sb.append("]");
		return sb.toString();
	}

}



