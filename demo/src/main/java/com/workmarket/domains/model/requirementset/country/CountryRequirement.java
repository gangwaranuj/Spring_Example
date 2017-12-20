package com.workmarket.domains.model.requirementset.country;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "country_requirement")
public class CountryRequirement extends AbstractRequirement {
	public static final String HUMAN_NAME = "Country";
	public static final String[] FILTERS = {};

	private CountryRequirable countryRequirable;

	@ManyToOne
	@JoinColumn(name = "country_id")
	@Fetch(FetchMode.JOIN)
	public CountryRequirable getCountryRequirable() {
		return countryRequirable;
	}

	public void setCountryRequirable(CountryRequirable countryRequirable) {
		this.countryRequirable = countryRequirable;
	}

	@Override
	@Transient
	public boolean allowMultiple() {
		return true;
	}

	@Override
	@Transient
	public String getHumanTypeName() {
		return HUMAN_NAME;
	}

	@Override
	@Transient
	public void accept(EligibilityVisitor visitor, Criterion criterion) {
		visitor.visit(criterion, this);
	}

	@Override
	@Transient
	public void accept(SolrQueryVisitor visitor, SolrQuery query) {
		visitor.visit(query, this);
	}
}
