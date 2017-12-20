package com.workmarket.domains.model.requirementset.traveldistance;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.geocoding.Coordinate;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@AuditChanges
@Table(name = "travel_distance_requirement")
public class TravelDistanceRequirement extends AbstractRequirement {
	public static final String NAME_TEMPLATE = "Within %s miles of %s";
	public static final String HUMAN_NAME = "Travel Distance";
	public static final String[] FILTERS = {};

	private Long distance;
	private String address;
	private Double latitude;
	private Double longitude;

	@Column(name = "distance")
	public Long getDistance() {
		return distance;
	}

	public void setDistance(Long distance) {
		this.distance = distance;
	}

	@Column(name = "address")
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	@Override
	@Transient
	public String getHumanTypeName() {
		return HUMAN_NAME;
	}

	@Override
	@Transient
	public boolean allowMultiple() {
		return true;
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

		@Column(name = "latitude")
	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	@Column(name = "longitude")
	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	@Transient
	public Coordinate getCoordinates() {
		return new Coordinate(longitude, latitude);
	}
}
