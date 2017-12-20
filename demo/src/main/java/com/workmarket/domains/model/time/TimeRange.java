package com.workmarket.domains.model.time;

import org.joda.time.DateTime;

public class TimeRange {
	
	//holds from and through
	private Long from;
	private Long through;
	
	//CONVENIENCE METHODS
	public DateTime getFromDateTime() {
		return new DateTime(from);
	}
	
	public DateTime getThroughDateTime() {
		return new DateTime(through);
	}
	
	//GETTERS/SETTERS
	public Long getFrom() {
		return from;
	}
	public void setFrom(Long from) {
		this.from = from;
	}
	public Long getThrough() {
		return through;
	}
	public void setThrough(Long through) {
		this.through = through;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((from == null) ? 0 : from.hashCode());
		result = prime * result + ((through == null) ? 0 : through.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TimeRange other = (TimeRange) obj;
		if (from == null) {
			if (other.from != null)
				return false;
		} else if (!from.equals(other.from))
			return false;
		if (through == null) {
			if (other.through != null)
				return false;
		} else if (!through.equals(other.through))
			return false;
		return true;
	}
	@Override
	public String toString() {
		return "TimeRange [from=" + from + ", through=" + through + "]";
	}
	
	
}
