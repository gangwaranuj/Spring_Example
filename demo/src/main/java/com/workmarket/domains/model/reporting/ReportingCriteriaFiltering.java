package com.workmarket.domains.model.reporting;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.CollectionUtilities;

@Entity(name="reportingCriteriaFiltering")
@Table(name="reporting_criteria_filtering")

@AuditChanges
public class ReportingCriteriaFiltering extends DeletableEntity {

	private String property;
	private String filteringType;
	private Calendar fromDate;
	private Calendar toDate;
	private BigDecimal fromValue;
	private BigDecimal toValue;
	private String contains;
	private String fieldValue;
	private String fromOperator;
	private String toOperator;
	private String fieldValueOperator;
    private ReportingCriteria reportingCriteria;

	private static final long serialVersionUID = -7740683706605127406L;


	public ReportingCriteriaFiltering(){
		super();
	}

	/**
	 * @return the fromDate
	 */
    @Column(name = "from_date", nullable = true)
	public Calendar getFromDate() {
		return fromDate;
	}

	/**
	 * @param fromDate the fromDate to set
	 */
	public void setFromDate(Calendar fromDate) {
		this.fromDate = fromDate;
	}

	/**
	 * @return the toDate
	 */
    @Column(name = "to_date", nullable = true)
	public Calendar getToDate() {
		return toDate;
	}

	/**
	 * @param toDate the toDate to set
	 */
	public void setToDate(Calendar toDate) {
		this.toDate = toDate;
	}

	@Column(name="from_value", nullable=true)
	public BigDecimal getFromValue() {
		return fromValue;
	}

	public void setFromValue(BigDecimal fromValue) {
		this.fromValue = fromValue;
	}

	@Column(name="to_value", nullable=true)
	public BigDecimal getToValue() {
		return toValue;
	}

	public void setToValue(BigDecimal toValue) {
		this.toValue = toValue;
	}


	@Column(name="contains", nullable=true)
	public String getContains() {
		return contains;
	}

	public void setContains(String contains) {
		this.contains = contains;
	}

	/**
	 * @return the property
	 */
	@Column(name="property", nullable=true)
	public String getProperty() {
		return property;
	}

	/**
	 * @param property the property to set
	 */
	public void setProperty(String property) {
		this.property = property;
	}

	/**
	 * @return the filteringType
	 */
	@Column(name="filtering_type", nullable=true)
	public String getFilteringType() {
		return filteringType;
	}

	/**
	 * @param filteringType the filteringType to set
	 */
	public void setFilteringType(String filteringType) {
		this.filteringType = filteringType;
	}

	/**
	 * @return the fieldValue
	 */
	@Column(name="field_value", nullable=true)
	public String getFieldValue() {
		return fieldValue;
	}

	/**
	 * @param fieldValue the fieldValue to set
	 */
	public void setFieldValue(String fieldValue) {
		this.fieldValue = fieldValue;
	}

	public void setFieldValue(List<String> fieldValues) {
		this.fieldValue = CollectionUtilities.join(fieldValues, ",");
	}

	/**
	 * @return the fromOperator
	 */
	@Column(name="from_operator", nullable=true)
	public String getFromOperator() {
		return fromOperator;
	}

	/**
	 * @param fromOperator the fromOperator to set
	 */
	public void setFromOperator(String fromOperator) {
		this.fromOperator = fromOperator;
	}

	/**
	 * @return the toOperator
	 */
	@Column(name="to_operator", nullable=true)
	public String getToOperator() {
		return toOperator;
	}

	/**
	 * @param toOperator the toOperator to set
	 */
	public void setToOperator(String toOperator) {
		this.toOperator = toOperator;
	}

	/**
	 * @return the fieldValueOperator
	 */
	@Column(name="field_value_operator", nullable=true)
	public String getFieldValueOperator() {
		return fieldValueOperator;
	}

	/**
	 * @param fieldValueOperator the fieldValueOperator to set
	 */
	public void setFieldValueOperator(String fieldValueOperator) {
		this.fieldValueOperator = fieldValueOperator;
	}


	/**
	 * @return the reportingCriteria
	 */
	@ManyToOne(fetch=FetchType.LAZY,cascade=CascadeType.ALL )
	@JoinColumn(name="reporting_criteria_id", referencedColumnName="id", nullable=false,insertable=true,updatable=false)
	public ReportingCriteria getReportingCriteria() {
		return reportingCriteria;
	}


	/**
	 * @param reportingCriteria the reportingCriteria to set
	 */
	public void setReportingCriteria(ReportingCriteria reportingCriteria) {
		this.reportingCriteria = reportingCriteria;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		StringBuilder sb = new StringBuilder("ReportingCriteria[");
		sb.append("id:" + getId());
		sb.append(", property:" + getProperty());
		sb.append("]");
		return sb.toString();
	}

}
