package com.workmarket.thrift.services.realtime;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RealtimeFilter implements Serializable {
	private static final long serialVersionUID = 1L;

	private TimeFilter timeToAppointment;
	private TimeFilter timeExpired;
	private short percentWithOffers;
	private short percentWithRejections;
	private short numberOfUnansweredQuestions;
	private short percentResourcesWhoViewedAssignment;
	private List<String> internalOwnerFilter;
	private List<Long> clientFilter;
	private List<Long> projectFilter;

	public RealtimeFilter() {
	}

	public TimeFilter getTimeToAppointment() {
		return this.timeToAppointment;
	}

	public RealtimeFilter setTimeToAppointment(TimeFilter timeToAppointment) {
		this.timeToAppointment = timeToAppointment;
		return this;
	}

	public boolean isSetTimeToAppointment() {
		return this.timeToAppointment != null;
	}

	public TimeFilter getTimeExpired() {
		return this.timeExpired;
	}

	public RealtimeFilter setTimeExpired(TimeFilter timeExpired) {
		this.timeExpired = timeExpired;
		return this;
	}

	public boolean isSetTimeExpired() {
		return this.timeExpired != null;
	}

	public short getPercentWithOffers() {
		return this.percentWithOffers;
	}

	public RealtimeFilter setPercentWithOffers(short percentWithOffers) {
		this.percentWithOffers = percentWithOffers;
		return this;
	}

	public boolean isSetPercentWithOffers() {
		return (percentWithOffers > 0);
	}

	public short getPercentWithRejections() {
		return this.percentWithRejections;
	}

	public RealtimeFilter setPercentWithRejections(short percentWithRejections) {
		this.percentWithRejections = percentWithRejections;
		return this;
	}

	public boolean isSetPercentWithRejections() {
		return (percentWithRejections > 0);
	}

	public short getNumberOfUnansweredQuestions() {
		return this.numberOfUnansweredQuestions;
	}

	public RealtimeFilter setNumberOfUnansweredQuestions(short numberOfUnansweredQuestions) {
		this.numberOfUnansweredQuestions = numberOfUnansweredQuestions;
		return this;
	}

	public boolean isSetNumberOfUnansweredQuestions() {
		return (numberOfUnansweredQuestions > 0);
	}

	public short getPercentResourcesWhoViewedAssignment() {
		return this.percentResourcesWhoViewedAssignment;
	}

	public RealtimeFilter setPercentResourcesWhoViewedAssignment(short percentResourcesWhoViewedAssignment) {
		this.percentResourcesWhoViewedAssignment = percentResourcesWhoViewedAssignment;
		return this;
	}

	public boolean isSetPercentResourcesWhoViewedAssignment() {
		return (percentResourcesWhoViewedAssignment > 0);
	}

	public int getInternalOwnerFilterSize() {
		return (this.internalOwnerFilter == null) ? 0 : this.internalOwnerFilter.size();
	}

	public java.util.Iterator<String> getInternalOwnerFilterIterator() {
		return (this.internalOwnerFilter == null) ? null : this.internalOwnerFilter.iterator();
	}

	public void addToInternalOwnerFilter(String elem) {
		if (this.internalOwnerFilter == null) {
			this.internalOwnerFilter = new ArrayList<String>();
		}
		this.internalOwnerFilter.add(elem);
	}

	public List<String> getInternalOwnerFilter() {
		return this.internalOwnerFilter;
	}

	public RealtimeFilter setInternalOwnerFilter(List<String> internalOwnerFilter) {
		this.internalOwnerFilter = internalOwnerFilter;
		return this;
	}

	public boolean isSetInternalOwnerFilter() {
		return this.internalOwnerFilter != null;
	}

	public int getClientFilterSize() {
		return (this.clientFilter == null) ? 0 : this.clientFilter.size();
	}

	public java.util.Iterator<Long> getClientFilterIterator() {
		return (this.clientFilter == null) ? null : this.clientFilter.iterator();
	}

	public void addToClientFilter(long elem) {
		if (this.clientFilter == null) {
			this.clientFilter = new ArrayList<Long>();
		}
		this.clientFilter.add(elem);
	}

	public List<Long> getClientFilter() {
		return this.clientFilter;
	}

	public RealtimeFilter setClientFilter(List<Long> clientFilter) {
		this.clientFilter = clientFilter;
		return this;
	}

	public boolean isSetClientFilter() {
		return this.clientFilter != null;
	}

	public int getProjectFilterSize() {
		return (this.projectFilter == null) ? 0 : this.projectFilter.size();
	}

	public java.util.Iterator<Long> getProjectFilterIterator() {
		return (this.projectFilter == null) ? null : this.projectFilter.iterator();
	}

	public void addToProjectFilter(long elem) {
		if (this.projectFilter == null) {
			this.projectFilter = new ArrayList<Long>();
		}
		this.projectFilter.add(elem);
	}

	public List<Long> getProjectFilter() {
		return this.projectFilter;
	}

	public RealtimeFilter setProjectFilter(List<Long> projectFilter) {
		this.projectFilter = projectFilter;
		return this;
	}

	public boolean isSetProjectFilter() {
		return this.projectFilter != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof RealtimeFilter)
			return this.equals((RealtimeFilter) that);
		return false;
	}

	private boolean equals(RealtimeFilter that) {
		if (that == null)
			return false;

		boolean this_present_timeToAppointment = true && this.isSetTimeToAppointment();
		boolean that_present_timeToAppointment = true && that.isSetTimeToAppointment();
		if (this_present_timeToAppointment || that_present_timeToAppointment) {
			if (!(this_present_timeToAppointment && that_present_timeToAppointment))
				return false;
			if (!this.timeToAppointment.equals(that.timeToAppointment))
				return false;
		}

		boolean this_present_timeExpired = true && this.isSetTimeExpired();
		boolean that_present_timeExpired = true && that.isSetTimeExpired();
		if (this_present_timeExpired || that_present_timeExpired) {
			if (!(this_present_timeExpired && that_present_timeExpired))
				return false;
			if (!this.timeExpired.equals(that.timeExpired))
				return false;
		}

		boolean this_present_percentWithOffers = true && this.isSetPercentWithOffers();
		boolean that_present_percentWithOffers = true && that.isSetPercentWithOffers();
		if (this_present_percentWithOffers || that_present_percentWithOffers) {
			if (!(this_present_percentWithOffers && that_present_percentWithOffers))
				return false;
			if (this.percentWithOffers != that.percentWithOffers)
				return false;
		}

		boolean this_present_percentWithRejections = true && this.isSetPercentWithRejections();
		boolean that_present_percentWithRejections = true && that.isSetPercentWithRejections();
		if (this_present_percentWithRejections || that_present_percentWithRejections) {
			if (!(this_present_percentWithRejections && that_present_percentWithRejections))
				return false;
			if (this.percentWithRejections != that.percentWithRejections)
				return false;
		}

		boolean this_present_numberOfUnansweredQuestions = true && this.isSetNumberOfUnansweredQuestions();
		boolean that_present_numberOfUnansweredQuestions = true && that.isSetNumberOfUnansweredQuestions();
		if (this_present_numberOfUnansweredQuestions || that_present_numberOfUnansweredQuestions) {
			if (!(this_present_numberOfUnansweredQuestions && that_present_numberOfUnansweredQuestions))
				return false;
			if (this.numberOfUnansweredQuestions != that.numberOfUnansweredQuestions)
				return false;
		}

		boolean this_present_percentResourcesWhoViewedAssignment = true && this.isSetPercentResourcesWhoViewedAssignment();
		boolean that_present_percentResourcesWhoViewedAssignment = true && that.isSetPercentResourcesWhoViewedAssignment();
		if (this_present_percentResourcesWhoViewedAssignment || that_present_percentResourcesWhoViewedAssignment) {
			if (!(this_present_percentResourcesWhoViewedAssignment && that_present_percentResourcesWhoViewedAssignment))
				return false;
			if (this.percentResourcesWhoViewedAssignment != that.percentResourcesWhoViewedAssignment)
				return false;
		}

		boolean this_present_internalOwnerFilter = true && this.isSetInternalOwnerFilter();
		boolean that_present_internalOwnerFilter = true && that.isSetInternalOwnerFilter();
		if (this_present_internalOwnerFilter || that_present_internalOwnerFilter) {
			if (!(this_present_internalOwnerFilter && that_present_internalOwnerFilter))
				return false;
			if (!this.internalOwnerFilter.equals(that.internalOwnerFilter))
				return false;
		}

		boolean this_present_clientFilter = true && this.isSetClientFilter();
		boolean that_present_clientFilter = true && that.isSetClientFilter();
		if (this_present_clientFilter || that_present_clientFilter) {
			if (!(this_present_clientFilter && that_present_clientFilter))
				return false;
			if (!this.clientFilter.equals(that.clientFilter))
				return false;
		}

		boolean this_present_projectFilter = true && this.isSetProjectFilter();
		boolean that_present_projectFilter = true && that.isSetProjectFilter();
		if (this_present_projectFilter || that_present_projectFilter) {
			if (!(this_present_projectFilter && that_present_projectFilter))
				return false;
			if (!this.projectFilter.equals(that.projectFilter))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_timeToAppointment = true && (isSetTimeToAppointment());
		builder.append(present_timeToAppointment);
		if (present_timeToAppointment)
			builder.append(timeToAppointment);

		boolean present_timeExpired = true && (isSetTimeExpired());
		builder.append(present_timeExpired);
		if (present_timeExpired)
			builder.append(timeExpired);

		boolean present_percentWithOffers = true && (isSetPercentWithOffers());
		builder.append(present_percentWithOffers);
		if (present_percentWithOffers)
			builder.append(percentWithOffers);

		boolean present_percentWithRejections = true && (isSetPercentWithRejections());
		builder.append(present_percentWithRejections);
		if (present_percentWithRejections)
			builder.append(percentWithRejections);

		boolean present_numberOfUnansweredQuestions = true && (isSetNumberOfUnansweredQuestions());
		builder.append(present_numberOfUnansweredQuestions);
		if (present_numberOfUnansweredQuestions)
			builder.append(numberOfUnansweredQuestions);

		boolean present_percentResourcesWhoViewedAssignment = true && (isSetPercentResourcesWhoViewedAssignment());
		builder.append(present_percentResourcesWhoViewedAssignment);
		if (present_percentResourcesWhoViewedAssignment)
			builder.append(percentResourcesWhoViewedAssignment);

		boolean present_internalOwnerFilter = true && (isSetInternalOwnerFilter());
		builder.append(present_internalOwnerFilter);
		if (present_internalOwnerFilter)
			builder.append(internalOwnerFilter);

		boolean present_clientFilter = true && (isSetClientFilter());
		builder.append(present_clientFilter);
		if (present_clientFilter)
			builder.append(clientFilter);

		boolean present_projectFilter = true && (isSetProjectFilter());
		builder.append(present_projectFilter);
		if (present_projectFilter)
			builder.append(projectFilter);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("RealtimeFilter(");
		boolean first = true;

		if (isSetTimeToAppointment()) {
			sb.append("timeToAppointment:");
			if (this.timeToAppointment == null) {
				sb.append("null");
			} else {
				sb.append(this.timeToAppointment);
			}
			first = false;
		}
		if (isSetTimeExpired()) {
			if (!first) sb.append(", ");
			sb.append("timeExpired:");
			if (this.timeExpired == null) {
				sb.append("null");
			} else {
				sb.append(this.timeExpired);
			}
			first = false;
		}
		if (isSetPercentWithOffers()) {
			if (!first) sb.append(", ");
			sb.append("percentWithOffers:");
			sb.append(this.percentWithOffers);
			first = false;
		}
		if (isSetPercentWithRejections()) {
			if (!first) sb.append(", ");
			sb.append("percentWithRejections:");
			sb.append(this.percentWithRejections);
			first = false;
		}
		if (isSetNumberOfUnansweredQuestions()) {
			if (!first) sb.append(", ");
			sb.append("numberOfUnansweredQuestions:");
			sb.append(this.numberOfUnansweredQuestions);
			first = false;
		}
		if (isSetPercentResourcesWhoViewedAssignment()) {
			if (!first) sb.append(", ");
			sb.append("percentResourcesWhoViewedAssignment:");
			sb.append(this.percentResourcesWhoViewedAssignment);
			first = false;
		}
		if (isSetInternalOwnerFilter()) {
			if (!first) sb.append(", ");
			sb.append("internalOwnerFilter:");
			if (this.internalOwnerFilter == null) {
				sb.append("null");
			} else {
				sb.append(this.internalOwnerFilter);
			}
			first = false;
		}
		if (isSetClientFilter()) {
			if (!first) sb.append(", ");
			sb.append("clientFilter:");
			if (this.clientFilter == null) {
				sb.append("null");
			} else {
				sb.append(this.clientFilter);
			}
			first = false;
		}
		if (isSetProjectFilter()) {
			if (!first) sb.append(", ");
			sb.append("projectFilter:");
			if (this.projectFilter == null) {
				sb.append("null");
			} else {
				sb.append(this.projectFilter);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}

