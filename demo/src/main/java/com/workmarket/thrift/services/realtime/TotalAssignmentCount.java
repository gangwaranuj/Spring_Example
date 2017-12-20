package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class TotalAssignmentCount implements Serializable {
	private static final long serialVersionUID = 1L;

	private int openAssignments;
	private int todaySentAssignments;
	private int todayCreatedAssignments;
	private int todayVoidedAssignments;
	private int todayCancelledAssignments;
	private int todayAcceptedAssignments;

	public TotalAssignmentCount() {
		this.openAssignments = 0;
		this.todaySentAssignments = 0;
		this.todayCreatedAssignments = 0;
		this.todayVoidedAssignments = 0;
		this.todayCancelledAssignments = 0;
		this.todayAcceptedAssignments = 0;
	}

	public TotalAssignmentCount(
			int openAssignments,
			int todaySentAssignments,
			int todayCreatedAssignments,
			int todayVoidedAssignments,
			int todayCancelledAssignments,
			int todayAcceptedAssignments) {
		this();
		this.openAssignments = openAssignments;
		this.todaySentAssignments = todaySentAssignments;
		this.todayCreatedAssignments = todayCreatedAssignments;
		this.todayVoidedAssignments = todayVoidedAssignments;
		this.todayCancelledAssignments = todayCancelledAssignments;
		this.todayAcceptedAssignments = todayAcceptedAssignments;
	}

	public int getOpenAssignments() {
		return this.openAssignments;
	}

	public TotalAssignmentCount setOpenAssignments(int openAssignments) {
		this.openAssignments = openAssignments;
		return this;
	}

	public int getTodaySentAssignments() {
		return this.todaySentAssignments;
	}

	public TotalAssignmentCount setTodaySentAssignments(int todaySentAssignments) {
		this.todaySentAssignments = todaySentAssignments;
		return this;
	}

	public int getTodayCreatedAssignments() {
		return this.todayCreatedAssignments;
	}

	public TotalAssignmentCount setTodayCreatedAssignments(int todayCreatedAssignments) {
		this.todayCreatedAssignments = todayCreatedAssignments;
		return this;
	}

	public int getTodayVoidedAssignments() {
		return this.todayVoidedAssignments;
	}

	public TotalAssignmentCount setTodayVoidedAssignments(int todayVoidedAssignments) {
		this.todayVoidedAssignments = todayVoidedAssignments;
		return this;
	}

	public int getTodayCancelledAssignments() {
		return this.todayCancelledAssignments;
	}

	public TotalAssignmentCount setTodayCancelledAssignments(int todayCancelledAssignments) {
		this.todayCancelledAssignments = todayCancelledAssignments;
		return this;
	}

	public int getTodayAcceptedAssignments() {
		return this.todayAcceptedAssignments;
	}

	public TotalAssignmentCount setTodayAcceptedAssignments(int todayAcceptedAssignments) {
		this.todayAcceptedAssignments = todayAcceptedAssignments;
		return this;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof TotalAssignmentCount)
			return this.equals((TotalAssignmentCount) that);
		return false;
	}

	private boolean equals(TotalAssignmentCount that) {
		if (that == null)
			return false;

		boolean this_present_openAssignments = true;
		boolean that_present_openAssignments = true;
		if (this_present_openAssignments || that_present_openAssignments) {
			if (!(this_present_openAssignments && that_present_openAssignments))
				return false;
			if (this.openAssignments != that.openAssignments)
				return false;
		}

		boolean this_present_todaySentAssignments = true;
		boolean that_present_todaySentAssignments = true;
		if (this_present_todaySentAssignments || that_present_todaySentAssignments) {
			if (!(this_present_todaySentAssignments && that_present_todaySentAssignments))
				return false;
			if (this.todaySentAssignments != that.todaySentAssignments)
				return false;
		}

		boolean this_present_todayCreatedAssignments = true;
		boolean that_present_todayCreatedAssignments = true;
		if (this_present_todayCreatedAssignments || that_present_todayCreatedAssignments) {
			if (!(this_present_todayCreatedAssignments && that_present_todayCreatedAssignments))
				return false;
			if (this.todayCreatedAssignments != that.todayCreatedAssignments)
				return false;
		}

		boolean this_present_todayVoidedAssignments = true;
		boolean that_present_todayVoidedAssignments = true;
		if (this_present_todayVoidedAssignments || that_present_todayVoidedAssignments) {
			if (!(this_present_todayVoidedAssignments && that_present_todayVoidedAssignments))
				return false;
			if (this.todayVoidedAssignments != that.todayVoidedAssignments)
				return false;
		}

		boolean this_present_todayCancelledAssignments = true;
		boolean that_present_todayCancelledAssignments = true;
		if (this_present_todayCancelledAssignments || that_present_todayCancelledAssignments) {
			if (!(this_present_todayCancelledAssignments && that_present_todayCancelledAssignments))
				return false;
			if (this.todayCancelledAssignments != that.todayCancelledAssignments)
				return false;
		}

		boolean this_present_todayAcceptedAssignments = true;
		boolean that_present_todayAcceptedAssignments = true;
		if (this_present_todayAcceptedAssignments || that_present_todayAcceptedAssignments) {
			if (!(this_present_todayAcceptedAssignments && that_present_todayAcceptedAssignments))
				return false;
			if (this.todayAcceptedAssignments != that.todayAcceptedAssignments)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_openAssignments = true;
		builder.append(present_openAssignments);
		if (present_openAssignments)
			builder.append(openAssignments);

		boolean present_todaySentAssignments = true;
		builder.append(present_todaySentAssignments);
		if (present_todaySentAssignments)
			builder.append(todaySentAssignments);

		boolean present_todayCreatedAssignments = true;
		builder.append(present_todayCreatedAssignments);
		if (present_todayCreatedAssignments)
			builder.append(todayCreatedAssignments);

		boolean present_todayVoidedAssignments = true;
		builder.append(present_todayVoidedAssignments);
		if (present_todayVoidedAssignments)
			builder.append(todayVoidedAssignments);

		boolean present_todayCancelledAssignments = true;
		builder.append(present_todayCancelledAssignments);
		if (present_todayCancelledAssignments)
			builder.append(todayCancelledAssignments);

		boolean present_todayAcceptedAssignments = true;
		builder.append(present_todayAcceptedAssignments);
		if (present_todayAcceptedAssignments)
			builder.append(todayAcceptedAssignments);

		return builder.toHashCode();
	}

}