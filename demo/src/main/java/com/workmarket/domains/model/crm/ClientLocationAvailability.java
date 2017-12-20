package com.workmarket.domains.model.crm;

import java.util.Calendar;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.workmarket.domains.model.user.UserAvailability;
import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.utility.DateUtilities;

@Entity(name="clientLocationAvailability")
@DiscriminatorValue("location")
@AuditChanges
public class ClientLocationAvailability extends UserAvailability {

	private static final long serialVersionUID = 1L;

	private ClientLocation clientLocation;

	public static final int DEFAULT_FROM_HOUR = 8;
	public static final int DEFAULT_FROM_MINUTE = 30;
	public static final int DEFAULT_TO_HOUR = 18;
	public static final int DEFAULT_TO_MINUTE = 30;

	public ClientLocationAvailability() {}

	public ClientLocationAvailability(ClientLocation clientLocation, Integer weekDay) {
		super(weekDay);
		this.clientLocation = clientLocation;
	}

	@ManyToOne(optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "client_location_id")
	public ClientLocation getClientLocation() {
		return clientLocation;
	}

	public void setClientLocation(ClientLocation clientLocation) {
		this.clientLocation = clientLocation;
	}

	public static Calendar getDefaultFromTime(String timeZoneId) {
		return DateUtilities.getCalendarWithTime(DEFAULT_FROM_HOUR, DEFAULT_FROM_MINUTE, timeZoneId);
	}

	public static Calendar getDefaultToTime(String timeZoneId) {
		return DateUtilities.getCalendarWithTime(DEFAULT_TO_HOUR, DEFAULT_TO_MINUTE, timeZoneId);
	}
}
