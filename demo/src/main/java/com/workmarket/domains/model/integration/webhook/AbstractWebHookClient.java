package com.workmarket.domains.model.integration.webhook;

import com.workmarket.domains.model.Company;
import com.workmarket.domains.model.audit.AuditedEntity;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Calendar;

@Entity(name = "webHookClient")
@Table(name = "web_hook_client")
@DiscriminatorColumn(name="type", discriminatorType=DiscriminatorType.STRING)
@DiscriminatorValue("base")
public class AbstractWebHookClient extends AuditedEntity implements Serializable {
	private static final long serialVersionUID = 3347547646441908481L;

	public static final String SALESFORCE = "salesforce";
	public static final String GENERIC = "generic";

	public enum DateFormat {
		UNIX("Unix Time Stamp"),
		ISO_8601("YYYY-MM-DDThh:mm:ssZ");

		private String title;

		private DateFormat(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public static DateFormat newInstance(String name) {
			return DateFormat.find(name);
		}

		private static DateFormat find(String name) {
			for (DateFormat type : DateFormat.values())
				if (type.toString().equalsIgnoreCase(name))
					return type;
			return null;
		}
	}

	private Company company;
	private DateFormat dateFormat = DateFormat.UNIX;
	private boolean suppressApiEvents = true;

	@ManyToOne(cascade = CascadeType.ALL)
	@Fetch(FetchMode.JOIN)
	@JoinColumn(name = "company_id", referencedColumnName = "id", updatable = false)
	public Company getCompany() {
		return company;
	}

	public AbstractWebHookClient setCompany(Company company) {
		this.company = company;
		return this;
	}

	@Column(name = "date_format", nullable = false)
	@Enumerated(value = EnumType.STRING)
	public DateFormat getDateFormat() {
		return dateFormat;
	}

	public AbstractWebHookClient setDateFormat(DateFormat dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}

	@Column(name="suppress_api_events")
	public boolean isSuppressApiEvents() {
		return suppressApiEvents;
	}

	public void setSuppressApiEvents(boolean suppressApiEvents) {
		this.suppressApiEvents = suppressApiEvents;
	}

	@Transient
	public String formatDate(Calendar calendar) {
		if (calendar == null)
			return StringUtils.EMPTY;

		if (getDateFormat() == AbstractWebHookClient.DateFormat.ISO_8601) {
			return DateUtilities.getISO8601(calendar);
		} else {
			// default to unix time stamp
			return String.valueOf(calendar.getTimeInMillis() / 1000L);
		}
	}
}
