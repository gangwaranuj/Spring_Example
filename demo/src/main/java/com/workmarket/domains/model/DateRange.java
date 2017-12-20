package com.workmarket.domains.model;

import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang3.builder.EqualsBuilder;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Calendar;

@Embeddable
public class DateRange implements Serializable {
	private static final long serialVersionUID = 6349492572674369470L;

	private Calendar from;
	private Calendar through;

	public DateRange() {
	}

	public DateRange(DateRange dateRange) {
		this(dateRange.getFrom(), dateRange.getThrough());
	}

	public DateRange(Calendar from) {
		this.from = DateUtilities.cloneCalendar(from);
	}

	public DateRange(Calendar from, Calendar through) {
		this.from = DateUtilities.cloneCalendar(from);
		this.through = DateUtilities.cloneCalendar(through);
	}

	public DateRange(Calendar from, Calendar time_from, Calendar through, Calendar time_through) {
		this.from = DateUtilities.setSpecificTime(from, time_from);
		this.through = DateUtilities.setSpecificTime(through, time_through);
	}

	public Calendar getFrom() {
		return from;
	}

	public void setFrom(Calendar from) {
		this.from = from;
	}

	public Calendar getThrough() {
		return through;
	}

	public void setThrough(Calendar through) {
		this.through = through;
	}

	@Transient
	public boolean isRange() {
		return from != null && through != null;
	}

	@Transient
	public boolean contains(DateRange range) {
		if (range == null) return false;
		if (!isRange()) return false;
		if (!range.isRange()) return DateUtilities.intervalContains(from, through, range.getFrom());
		return DateUtilities.intervalContains(from, through, range.getFrom()) && DateUtilities.intervalContains(from, through, range.getThrough());
	}

	@Transient
	public boolean overlaps(DateRange range) {
		if (range == null) return false;
		if (!isRange()) return false;
		if (!range.isRange()) return DateUtilities.intervalContains(from, through, range.getFrom());
		return DateUtilities.intervalContains(from, through, range.getFrom()) || DateUtilities.intervalContains(from, through, range.getThrough());
	}

	@Transient
	public boolean contains(Calendar calendar) {
		if (calendar == null) return false;
		if (!isRange()) return false;
		return DateUtilities.intervalContains(from, through, calendar);
	}

	@Transient
	public int getDaysBetweenDateRange() {
		if (!isRange()) return 0;
		return DateUtilities.getDaysBetween(from, through);
	}

	// Date Ranges may contain calendars that have different Timezones but are otherwise equal
	// this method uses Calendar.compareTo() method which compares using ticks since Epoch
	@Override
	public boolean equals(Object obj) {
		if (obj == null) { return false; }
		if (getClass() != obj.getClass()) { return false; }
		if (obj == this) { return true; }
		DateRange rhs = (DateRange) obj;
		return
			(((this.getFrom() == null && rhs.getFrom() == null) ||
			 (this.getFrom() != null && rhs.getFrom() != null && this.getFrom().compareTo(rhs.getFrom()) == 0)) &&
			((this.getThrough() == null && rhs.getThrough() == null) ||
			 (this.getThrough() != null && rhs.getThrough() != null && this.getThrough().compareTo(rhs.getThrough()) == 0)));
	}

	@Override
	public int hashCode() {
		int result = from != null ? from.hashCode() : 0;

		if (through != null) {
			result = 31 * result + through.hashCode();
		}
		return result;
	}

	@Override
	public String toString() {
		return "DateRange{" +
				"from=" + from +
				", through=" + through +
				'}';
	}
}
