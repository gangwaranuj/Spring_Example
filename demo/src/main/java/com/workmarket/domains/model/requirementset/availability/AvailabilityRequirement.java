package com.workmarket.domains.model.requirementset.availability;

import com.workmarket.domains.model.audit.AuditChanges;
import com.workmarket.domains.model.requirementset.AbstractRequirement;
import com.workmarket.domains.model.requirementset.Criterion;
import com.workmarket.domains.model.requirementset.EligibilityVisitor;
import com.workmarket.domains.model.requirementset.SolrQueryVisitor;
import org.apache.solr.client.solrj.SolrQuery;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.Pattern;

@Entity
@AuditChanges
@Table(name = "availability_requirement")
public class AvailabilityRequirement extends AbstractRequirement {
	public static final String NAME_TEMPLATE = "%s - %s to %s";
	public static final String HUMAN_NAME = "Availability";
	public static final String[] FILTERS = {};

	private WeekdayRequirable weekdayRequirable;
	private Integer dayOfWeek;
	private String fromTime;
	private String toTime;

	@Transient
	public WeekdayRequirable getWeekdayRequirable() {
		return weekdayRequirable != null ? weekdayRequirable : createWeekdayRequirable();
	}

	@Transient
	public void setWeekdayRequirable(WeekdayRequirable weekdayRequirable) {
		this.dayOfWeek = weekdayRequirable.getId();
		this.weekdayRequirable = weekdayRequirable;
	}

	@Column(name = "day_of_week")
	public Integer getDayOfWeek() {
		return this.dayOfWeek;
	}

	public void setDayOfWeek(Integer dayOfWeek) {
		this.dayOfWeek = dayOfWeek;
	}

	@Pattern(regexp = "^(0?[1-9]|1[0-2]):([0-5][0-9])(am|AM|pm|PM)$")
	@Column(name = "from_time")
	public String getFromTime() {
		return fromTime;
	}

	public void setFromTime(String fromTime) {
		this.fromTime = fromTime;
	}

	@Pattern(regexp = "^(0?[1-9]|1[0-2]):([0-5][0-9])(am|AM|pm|PM)$")
	@Column(name = "to_time")
	public String getToTime() {
		return toTime;
	}

	public void setToTime(String toTime) {
		this.toTime = toTime;
	}

	@Transient
	public String getName() {
		return String.format(
			NAME_TEMPLATE,
			this.getWeekdayRequirable().getName(),
			this.fromTime,
			this.toTime
		);
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
	@Deprecated
	public void accept(SolrQueryVisitor visitor, SolrQuery query) {
		// We are slowly deprecating the Availability requirement
		return;
	}

	private WeekdayRequirable createWeekdayRequirable() {
		return new WeekdayRequirable(Weekday.getById(this.getDayOfWeek()));
	}
}
