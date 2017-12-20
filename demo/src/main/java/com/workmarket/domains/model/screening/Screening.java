package com.workmarket.domains.model.screening;
import com.workmarket.domains.model.AbstractEntity;
import com.workmarket.domains.model.User;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Calendar;

@Entity(name="screening")
@Table(name="screening")
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue(Screening.BASE_TYPE)
public abstract class Screening extends AbstractEntity {

	public static final String BASE_TYPE = "base";
	public static final String BACKGROUND_CHECK_TYPE = "background";
	public static final String DRUG_TEST_TYPE = "drug";
	public static final String INSURANCE = "insurance";
	public static final String CERTIFICATION = "certification";
	public static final String LICENSE = "license";
	public static final String CREDIT_CHECK_TYPE = "credit";

	private static final long serialVersionUID = 1L;

	private User user;
	private ScreeningStatusType screeningStatusType;
	private Calendar requestDate;
	private Calendar responseDate;
	private String screeningId;


	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="user_id", referencedColumnName="id", nullable=true)
	public User getUser() {
		return user;
	}

	@ManyToOne(fetch=FetchType.EAGER,cascade=CascadeType.ALL)
	@JoinColumn(name="screening_status_type_code", referencedColumnName="code", nullable=true)
	public ScreeningStatusType getScreeningStatusType() {
		return screeningStatusType;
	}

	@Column(name = "request_date", nullable = false)
	public Calendar getRequestDate() {
		return requestDate;
	}

	@Column(name = "response_date", nullable = true)
	public Calendar getResponseDate() {
		return responseDate;
	}

	@Column(name = "vendor_request_id", nullable = true)
	public String getScreeningId() {
		return screeningId;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setScreeningStatusType(ScreeningStatusType screeningStatusType) {
		this.screeningStatusType = screeningStatusType;
	}

	public void setRequestDate(Calendar requestDate) {
		this.requestDate = requestDate;
	}

	public void setResponseDate(Calendar responseDate) {
		this.responseDate = responseDate;
	}

	public void setScreeningId(String screeningId) {
		this.screeningId = screeningId;
	}
}
