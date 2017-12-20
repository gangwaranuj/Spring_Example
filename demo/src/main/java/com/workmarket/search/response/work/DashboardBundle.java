package com.workmarket.search.response.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * User: micah
 * Date: 12/26/14
 * Time: 2:39 PM
 */
public class DashboardBundle implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;
	private String title;
	private boolean filteredOn;

	public DashboardBundle() {}


	public long getId() { return this.id; }

	public DashboardBundle setId(long id) {
		this.id = id;
		return this;
	}

	public boolean isSetId() { return id > 0L; }

	public String getTitle() { return this.title; }

	public DashboardBundle setTitle(String title) {
		this.title = title;
		return this;
	}

	public boolean isSetTitle() { return this.title != null; }

	public boolean isFilteredOn() { return this.filteredOn; }

	public void setFilteredOn(boolean filteredOn) { this.filteredOn = filteredOn; }

	@Override
	public boolean equals(Object that) {
		if (that == null || !(that instanceof  DashboardBundle)) { return false; }
		return this.equals((DashboardBundle) that);
	}

	private boolean equals(DashboardBundle that) {
		if (
			(this.isSetId() && !that.isSetId()) ||
			(!this.isSetId() && that.isSetId()) ||
			(this.isSetId() && that.isSetId() && this.getId() != that.getId())
		) {
			return false;
		}

		if (
			(this.isSetTitle() && !that.isSetTitle()) ||
			(!this.isSetTitle() && !that.isSetTitle()) ||
			(this.isSetTitle() && that.isSetTitle() && !this.title.equals(that.getTitle()))
		) {
			return false;
		}

		if (
			(this.isFilteredOn() && !that.isFilteredOn()) ||
			(!this.isFilteredOn() && that.isFilteredOn())
		) {
			return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();
		builder.append(isSetId());
		builder.append(isSetTitle());
		builder.append(isFilteredOn());

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("DashboardBundle(");

		if (isSetId()) { sb.append(id + ":"); }

		if (isSetTitle()) { sb.append(title + ":"); }

		sb.append("" + filteredOn);

		sb.append(")");
		return sb.toString();
	}
}
