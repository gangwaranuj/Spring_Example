package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkReportEntityBucketsCompositeResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<WorkReportEntityBucketResponse> workReportEntityBucketResponses;

	public WorkReportEntityBucketsCompositeResponse() {
	}

	public WorkReportEntityBucketsCompositeResponse(List<WorkReportEntityBucketResponse> workReportEntityBucketResponses) {
		this();
		this.workReportEntityBucketResponses = workReportEntityBucketResponses;
	}

	public int getWorkReportEntityBucketResponsesSize() {
		return (this.workReportEntityBucketResponses == null) ? 0 : this.workReportEntityBucketResponses.size();
	}

	public java.util.Iterator<WorkReportEntityBucketResponse> getWorkReportEntityBucketResponsesIterator() {
		return (this.workReportEntityBucketResponses == null) ? null : this.workReportEntityBucketResponses.iterator();
	}

	public void addToWorkReportEntityBucketResponses(WorkReportEntityBucketResponse elem) {
		if (this.workReportEntityBucketResponses == null) {
			this.workReportEntityBucketResponses = new ArrayList<WorkReportEntityBucketResponse>();
		}
		this.workReportEntityBucketResponses.add(elem);
	}

	public List<WorkReportEntityBucketResponse> getWorkReportEntityBucketResponses() {
		return this.workReportEntityBucketResponses;
	}

	public WorkReportEntityBucketsCompositeResponse setWorkReportEntityBucketResponses(List<WorkReportEntityBucketResponse> workReportEntityBucketResponses) {
		this.workReportEntityBucketResponses = workReportEntityBucketResponses;
		return this;
	}

	public boolean isSetWorkReportEntityBucketResponses() {
		return this.workReportEntityBucketResponses != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkReportEntityBucketsCompositeResponse)
			return this.equals((WorkReportEntityBucketsCompositeResponse) that);
		return false;
	}

	private boolean equals(WorkReportEntityBucketsCompositeResponse that) {
		if (that == null)
			return false;

		boolean this_present_workReportEntityBucketResponses = true && this.isSetWorkReportEntityBucketResponses();
		boolean that_present_workReportEntityBucketResponses = true && that.isSetWorkReportEntityBucketResponses();
		if (this_present_workReportEntityBucketResponses || that_present_workReportEntityBucketResponses) {
			if (!(this_present_workReportEntityBucketResponses && that_present_workReportEntityBucketResponses))
				return false;
			if (!this.workReportEntityBucketResponses.equals(that.workReportEntityBucketResponses))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workReportEntityBucketResponses = true && (isSetWorkReportEntityBucketResponses());
		builder.append(present_workReportEntityBucketResponses);
		if (present_workReportEntityBucketResponses)
			builder.append(workReportEntityBucketResponses);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkReportEntityBucketsCompositeResponse(");
		boolean first = true;

		sb.append("workReportEntityBucketResponses:");
		if (this.workReportEntityBucketResponses == null) {
			sb.append("null");
		} else {
			sb.append(this.workReportEntityBucketResponses);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}