package com.workmarket.domains.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

@Entity(name="twilioSourceNumber")
@Table(name="twilio_source_number")
public class TwilioSourceNumber extends AbstractEntity {

	private String sourceNumber;

	@Column(name="source_number", nullable = false, length = 25)
	public String getSourceNumber() {
		return sourceNumber;
	}

	public void setSourceNumber(String sourceNumber) {
		this.sourceNumber = sourceNumber;
	}
}
