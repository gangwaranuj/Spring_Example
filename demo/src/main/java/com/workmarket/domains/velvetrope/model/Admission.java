package com.workmarket.domains.velvetrope.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.workmarket.domains.model.DeletableEntity;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.velvetrope.Venue;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Set;

@Entity(name = "admission")
@Table(name = "admission")
@AuditChanges
@JsonIgnoreProperties({
	"createdOn", "createdOnString", "creatorId", "deleted", "encryptedId",
	"idHash", "modifiedOn", "modifiedOnString", "modifierId", "providedVenues"
})
public class Admission extends DeletableEntity {
	private String keyName;
	private String value;
	private Venue venue;

	@Column(name = "key_name")
	public String getKeyName() {
		return keyName;
	}

	public void setKeyName(String keyName) {
		this.keyName = keyName;
	}

	@Column(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Transient
	public Long getLongValue() {
		return Long.valueOf(value);
	}

	@Column(name = "venue")
	@Enumerated(EnumType.STRING)
	public Venue getVenue() {
		return venue;
	}

	public void setVenue(Venue venue) {
		this.venue = venue;
	}

	@Transient
	public Set<Venue> getProvidedVenues() {
		return venue.getProvidedVenues();
	}

	@Transient
	public int mask() {
		return venue.mask();
	}
}
