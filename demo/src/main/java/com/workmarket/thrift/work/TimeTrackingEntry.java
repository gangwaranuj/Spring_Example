package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class TimeTrackingEntry implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private long checkedInOn;
	private long checkedOutOn;
	private long createdOn;
	private long modifiedOn;
	private com.workmarket.thrift.core.User checkedInBy;
	private com.workmarket.thrift.core.User checkedOutBy;
	private com.workmarket.thrift.core.Note note;
	private Double distanceIn;
	private Double distanceOut;

	public TimeTrackingEntry() {
	}

	public TimeTrackingEntry(
			long id,
			long createdOn,
			long modifiedOn,
			long checkedInOn,
			long checkedOutOn,
			com.workmarket.thrift.core.User checkedInBy,
			com.workmarket.thrift.core.User checkedOutBy,
			com.workmarket.thrift.core.Note note,
			Double distanceIn,
			Double distanceOut) {
		this();
		this.id = id;
		this.checkedInOn = checkedInOn;
		this.checkedOutOn = checkedOutOn;
		this.checkedInBy = checkedInBy;
		this.checkedOutBy = checkedOutBy;
		this.createdOn = createdOn;
		this.modifiedOn = modifiedOn;
		this.note = note;
		this.distanceIn = distanceIn;
		this.distanceOut = distanceOut;
	}

	public long getId() {
		return this.id;
	}

	public TimeTrackingEntry setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() {
		return (id > 0L);
	}

	public long getCreatedOn() {
		return this.createdOn;
	}

	public TimeTrackingEntry setCreatedOn(long createdOn) {
		this.createdOn = createdOn;
		return this;
	}

	public long getModifiedOn() {
		return this.modifiedOn;
	}

	public TimeTrackingEntry setModifiedOn(long modifiedOn) {
		this.modifiedOn = modifiedOn;
		return this;
	}

	public long getCheckedInOn() {
		return this.checkedInOn;
	}

	public TimeTrackingEntry setCheckedInOn(long checkedInOn) {
		this.checkedInOn = checkedInOn;
		return this;
	}

	public long getCheckedOutOn() {
		return this.checkedOutOn;
	}

	public TimeTrackingEntry setCheckedOutOn(long checkedOutOn) {
		this.checkedOutOn = checkedOutOn;
		return this;
	}

	public com.workmarket.thrift.core.User getCheckedInBy() {
		return this.checkedInBy;
	}

	public TimeTrackingEntry setCheckedInBy(com.workmarket.thrift.core.User checkedInBy) {
		this.checkedInBy = checkedInBy;
		return this;
	}

	public boolean isSetCheckedInBy() {
		return this.checkedInBy != null;
	}

	public com.workmarket.thrift.core.User getCheckedOutBy() {
		return this.checkedOutBy;
	}

	public TimeTrackingEntry setCheckedOutBy(com.workmarket.thrift.core.User checkedOutBy) {
		this.checkedOutBy = checkedOutBy;
		return this;
	}

	public boolean isSetCheckedOutBy() {
		return this.checkedOutBy != null;
	}

	public com.workmarket.thrift.core.Note getNote() {
		return this.note;
	}

	public TimeTrackingEntry setNote(com.workmarket.thrift.core.Note note) {
		this.note = note;
		return this;
	}

	public boolean isSetNote() {
		return this.note != null;
	}

	public Double getDistanceIn() {
		return this.distanceIn;
	}

	public TimeTrackingEntry setDistanceIn(Double distance) {
		this.distanceIn = distance;
		return this;
	}

	public boolean isSetDistanceIn() {
		return this.distanceIn != null;
	}

	public Double getDistanceOut() {
		return this.distanceOut;
	}

	public TimeTrackingEntry setDistanceOut(Double distance) {
		this.distanceOut = distance;
		return this;
	}

	public boolean isSetDistanceOut () {
		return this.distanceOut != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof TimeTrackingEntry)
			return this.equals((TimeTrackingEntry) that);
		return false;
	}

	private boolean equals(TimeTrackingEntry that) {
		if (that == null)
			return false;

		boolean this_present_id = true;
		boolean that_present_id = true;
		if (this_present_id || that_present_id) {
			if (!(this_present_id && that_present_id))
				return false;
			if (this.id != that.id)
				return false;
		}

		boolean this_present_checkedInOn = true;
		boolean that_present_checkedInOn = true;
		if (this_present_checkedInOn || that_present_checkedInOn) {
			if (!(this_present_checkedInOn && that_present_checkedInOn))
				return false;
			if (this.checkedInOn != that.checkedInOn)
				return false;
		}

		boolean this_present_checkedOutOn = true;
		boolean that_present_checkedOutOn = true;
		if (this_present_checkedOutOn || that_present_checkedOutOn) {
			if (!(this_present_checkedOutOn && that_present_checkedOutOn))
				return false;
			if (this.checkedOutOn != that.checkedOutOn)
				return false;
		}

		boolean this_present_checkedInBy = true && this.isSetCheckedInBy();
		boolean that_present_checkedInBy = true && that.isSetCheckedInBy();
		if (this_present_checkedInBy || that_present_checkedInBy) {
			if (!(this_present_checkedInBy && that_present_checkedInBy))
				return false;
			if (!this.checkedInBy.equals(that.checkedInBy))
				return false;
		}

		boolean this_present_checkedOutBy = true && this.isSetCheckedOutBy();
		boolean that_present_checkedOutBy = true && that.isSetCheckedOutBy();
		if (this_present_checkedOutBy || that_present_checkedOutBy) {
			if (!(this_present_checkedOutBy && that_present_checkedOutBy))
				return false;
			if (!this.checkedOutBy.equals(that.checkedOutBy))
				return false;
		}

		boolean this_present_note = true && this.isSetNote();
		boolean that_present_note = true && that.isSetNote();
		if (this_present_note || that_present_note) {
			if (!(this_present_note && that_present_note))
				return false;
			if (!this.note.equals(that.note))
				return false;
		}

		boolean this_present_distance_in = true && this.isSetDistanceIn();
		boolean that_present_distance_in = true && that.isSetDistanceIn();
		if (this_present_distance_in || that_present_distance_in) {
			if (!(this_present_distance_in && that_present_distance_in))
				return false;
			if (this.distanceIn != that.distanceIn)
				return false;
		}

		boolean this_present_distance_out = true && this.isSetDistanceOut();
		boolean that_present_distance_out = true && that.isSetDistanceOut();
		if (this_present_distance_out || that_present_distance_out) {
			if (!(this_present_distance_out && that_present_distance_out))
				return false;
			if (this.distanceIn != that.distanceIn)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_id = true;
		builder.append(present_id);
		if (present_id)
			builder.append(id);

		boolean present_checkedInOn = true;
		builder.append(present_checkedInOn);
		if (present_checkedInOn)
			builder.append(checkedInOn);

		boolean present_checkedOutOn = true;
		builder.append(present_checkedOutOn);
		if (present_checkedOutOn)
			builder.append(checkedOutOn);

		boolean present_checkedInBy = true && (isSetCheckedInBy());
		builder.append(present_checkedInBy);
		if (present_checkedInBy)
			builder.append(checkedInBy);

		boolean present_checkedOutBy = true && (isSetCheckedOutBy());
		builder.append(present_checkedOutBy);
		if (present_checkedOutBy)
			builder.append(checkedOutBy);

		boolean present_note = true && (isSetNote());
		builder.append(present_note);
		if (present_note)
			builder.append(note);

		boolean present_distance_in = true && (isSetDistanceIn());
		builder.append(present_distance_in);
		if (present_distance_in)
			builder.append(distanceIn);

		boolean present_distance_out = true && (isSetDistanceOut());
			builder.append(present_distance_out);
			if (present_distance_out)
				builder.append(distanceOut);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("TimeTrackingEntry(");
		boolean first = true;

		sb.append("id:");
		sb.append(this.id);
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkedInOn:");
		sb.append(this.checkedInOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkedOutOn:");
		sb.append(this.checkedOutOn);
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkedInBy:");
		if (this.checkedInBy == null) {
			sb.append("null");
		} else {
			sb.append(this.checkedInBy);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("checkedOutBy:");
		if (this.checkedOutBy == null) {
			sb.append("null");
		} else {
			sb.append(this.checkedOutBy);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("note:");
		if (this.note == null) {
			sb.append("null");
		} else {
			sb.append(this.note);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("distanceIn:");
		if (this.distanceIn == null) {
			sb.append("null");
		} else {
			sb.append(this.distanceIn);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("distanceOut:");
		if (this.distanceOut == null) {
			sb.append("null");
		} else {
			sb.append(this.distanceOut);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}