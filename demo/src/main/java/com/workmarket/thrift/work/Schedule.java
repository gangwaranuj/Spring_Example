package com.workmarket.thrift.work;

import com.workmarket.domains.model.datetime.TimeZone;
import com.workmarket.utility.DateUtilities;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class Schedule implements Serializable {
	private static final long serialVersionUID = 1L;

	private long from;
	private long through;
	private boolean range;
	private long appointmentTime;

	public Schedule() {
	}

	public Schedule(long from, long through, boolean range, long appointmentTime) {
		this();
		this.from = from;
		this.through = through;
		this.range = range;
		this.appointmentTime = appointmentTime;
	}

	// copy constructor for changing the time zone
	public Schedule(Schedule schedule, TimeZone timeZone) {
		this(schedule.getFrom(), schedule.getThrough(), schedule.isRange(), schedule.getAppointmentTime());
		if (timeZone != null) {
			if (schedule.isSetFrom()) {
				this.from = DateUtilities.changeTimeZoneRetainFields(schedule.getFrom(), timeZone.getTimeZoneId());
			}
			if (schedule.isSetThrough()) {
				this.through = DateUtilities.changeTimeZoneRetainFields(schedule.getThrough(), timeZone.getTimeZoneId());
			}
			if (schedule.isSetAppointmentTime()) {
				this.appointmentTime = DateUtilities.changeTimeZoneRetainFields(schedule.getAppointmentTime(), timeZone.getTimeZoneId());
			}
		}
	}

	public long getFrom() {
		return this.from;
	}

	public Schedule setFrom(long from) {
		this.from = from;
		return this;
	}

	public boolean isSetFrom() {
		return (from > 0L);
	}

	public long getThrough() {
		return this.through;
	}

	public Schedule setThrough(long through) {
		this.through = through;
		return this;
	}

	public boolean isSetThrough() {
		return (through > 0L);
	}

	public boolean isRange() {
		return this.range;
	}

	public Schedule setRange(boolean range) {
		this.range = range;
		return this;
	}

	public long getAppointmentTime() {
		return this.appointmentTime;
	}

	public Schedule setAppointmentTime(long appointmentTime) {
		this.appointmentTime = appointmentTime;
		return this;
	}

	public boolean isSetAppointmentTime() {
		return (appointmentTime > 0L);
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof Schedule)
			return this.equals((Schedule) that);
		return false;
	}

	private boolean equals(Schedule that) {
		if (that == null)
			return false;

		boolean this_present_from = true;
		boolean that_present_from = true;
		if (this_present_from || that_present_from) {
			if (!(this_present_from && that_present_from))
				return false;
			if (this.from != that.from)
				return false;
		}

		boolean this_present_through = true;
		boolean that_present_through = true;
		if (this_present_through || that_present_through) {
			if (!(this_present_through && that_present_through))
				return false;
			if (this.through != that.through)
				return false;
		}

		boolean this_present_range = true;
		boolean that_present_range = true;
		if (this_present_range || that_present_range) {
			if (!(this_present_range && that_present_range))
				return false;
			if (this.range != that.range)
				return false;
		}

		boolean this_present_appointmentTime = true;
		boolean that_present_appointmentTime = true;
		if (this_present_appointmentTime || that_present_appointmentTime) {
			if (!(this_present_appointmentTime && that_present_appointmentTime))
				return false;
			if (this.appointmentTime != that.appointmentTime)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_from = true;
		builder.append(present_from);
		if (present_from)
			builder.append(from);

		boolean present_through = true;
		builder.append(present_through);
		if (present_through)
			builder.append(through);

		boolean present_range = true;
		builder.append(present_range);
		if (present_range)
			builder.append(range);

		boolean present_appointmentTime = true;
		builder.append(present_appointmentTime);
		if (present_appointmentTime)
			builder.append(appointmentTime);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("Schedule(");
		boolean first = true;

		sb.append("from:");
		sb.append(this.from);
		first = false;
		if (!first) sb.append(", ");
		sb.append("through:");
		sb.append(this.through);
		first = false;
		if (!first) sb.append(", ");
		sb.append("range:");
		sb.append(this.range);
		first = false;
		if (!first) sb.append(", ");
		sb.append("appointmentTime:");
		sb.append(this.appointmentTime);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}
