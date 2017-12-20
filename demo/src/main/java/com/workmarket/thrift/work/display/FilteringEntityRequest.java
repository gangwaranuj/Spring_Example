package com.workmarket.thrift.work.display;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.workmarket.domains.model.reporting.CustomFieldEntity;
import org.apache.commons.lang.builder.HashCodeBuilder;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

public class FilteringEntityRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private ReportingTypeRequest reportingTypeRequest;
	private List<ColumnValuesRequest> columnValuesRequests;
	private String reportName;
	private String reportKey;
	private PaginationPageThrift paginationPageThrift;
	private boolean generateReport;
	private Long masqueradeUserId;
	private List<Long> workCustomFieldIds;

	public FilteringEntityRequest() {
	}

	public FilteringEntityRequest(
			ReportingTypeRequest reportingTypeRequest,
			List<ColumnValuesRequest> columnValuesRequests,
			String reportName,
			PaginationPageThrift paginationPageThrift,
			boolean generateReport) {
		this();
		this.reportingTypeRequest = reportingTypeRequest;
		this.columnValuesRequests = columnValuesRequests;
		this.reportName = reportName;
		this.paginationPageThrift = paginationPageThrift;
		this.generateReport = generateReport;
	}

	public List<Long> getWorkCustomFieldIds() {
		return workCustomFieldIds;
	}

	public FilteringEntityRequest setWorkCustomFieldIds(List<Long> workCustomFieldIds) {
		this.workCustomFieldIds = workCustomFieldIds;
		return this;
	}

	public ReportingTypeRequest getReportingTypeRequest() {
		return this.reportingTypeRequest;
	}

	public FilteringEntityRequest setReportingTypeRequest(ReportingTypeRequest reportingTypeRequest) {
		this.reportingTypeRequest = reportingTypeRequest;
		return this;
	}

	public boolean isSetReportingTypeRequest() {
		return this.reportingTypeRequest != null;
	}

	public int getColumnValuesRequestsSize() {
		return (this.columnValuesRequests == null) ? 0 : this.columnValuesRequests.size();
	}

	public java.util.Iterator<ColumnValuesRequest> getColumnValuesRequestsIterator() {
		return (this.columnValuesRequests == null) ? null : this.columnValuesRequests.iterator();
	}

	public void addToColumnValuesRequests(ColumnValuesRequest elem) {
		if (this.columnValuesRequests == null) {
			this.columnValuesRequests = new ArrayList<ColumnValuesRequest>();
		}
		this.columnValuesRequests.add(elem);
	}

	public List<ColumnValuesRequest> getColumnValuesRequests() {
		return this.columnValuesRequests;
	}

	public FilteringEntityRequest setColumnValuesRequests(List<ColumnValuesRequest> columnValuesRequests) {
		this.columnValuesRequests = columnValuesRequests;
		return this;
	}

	public boolean isSetColumnValuesRequests() {
		return this.columnValuesRequests != null;
	}

	public String getReportName() {
		return this.reportName;
	}

	public FilteringEntityRequest setReportName(String reportName) {
		this.reportName = reportName;
		return this;
	}

	public String getReportKey() {
		return reportKey;
	}

	public FilteringEntityRequest setReportKey(String reportKey) {
		this.reportKey = reportKey;
		return this;
	}

	public boolean isSetReportName() {
		return this.reportName != null;
	}

	public PaginationPageThrift getPaginationPageThrift() {
		return this.paginationPageThrift;
	}

	public FilteringEntityRequest setPaginationPageThrift(PaginationPageThrift paginationPageThrift) {
		this.paginationPageThrift = paginationPageThrift;
		return this;
	}

	public boolean isSetPaginationPageThrift() {
		return this.paginationPageThrift != null;
	}

	public boolean isGenerateReport() {
		return this.generateReport;
	}

	public FilteringEntityRequest setGenerateReport(boolean generateReport) {
		this.generateReport = generateReport;
		return this;
	}

	public Long getMasqueradeUserId() {
		return masqueradeUserId;
	}

	public void setMasqueradeUserId(Long masqueradeUserId) {
		this.masqueradeUserId = masqueradeUserId;
	}

	public boolean hasCustomFields() {
		return isNotEmpty(columnValuesRequests) && Iterables.any(columnValuesRequests, new Predicate<ColumnValuesRequest>() {
			@Override public boolean apply(@Nullable ColumnValuesRequest columnValuesRequest) {
				return columnValuesRequest != null && columnValuesRequest.getKeyName() != null
						&& columnValuesRequest.getKeyName().startsWith(CustomFieldEntity.WORK_CUSTOM_FIELDS);
			}
		});
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FilteringEntityRequest)
			return this.equals((FilteringEntityRequest) that);
		return false;
	}

	private boolean equals(FilteringEntityRequest that) {
		if (that == null)
			return false;

		boolean this_present_reportingTypeRequest = true && this.isSetReportingTypeRequest();
		boolean that_present_reportingTypeRequest = true && that.isSetReportingTypeRequest();
		if (this_present_reportingTypeRequest || that_present_reportingTypeRequest) {
			if (!(this_present_reportingTypeRequest && that_present_reportingTypeRequest))
				return false;
			if (!this.reportingTypeRequest.equals(that.reportingTypeRequest))
				return false;
		}

		boolean this_present_columnValuesRequests = true && this.isSetColumnValuesRequests();
		boolean that_present_columnValuesRequests = true && that.isSetColumnValuesRequests();
		if (this_present_columnValuesRequests || that_present_columnValuesRequests) {
			if (!(this_present_columnValuesRequests && that_present_columnValuesRequests))
				return false;
			if (!this.columnValuesRequests.equals(that.columnValuesRequests))
				return false;
		}

		boolean this_present_reportName = true && this.isSetReportName();
		boolean that_present_reportName = true && that.isSetReportName();
		if (this_present_reportName || that_present_reportName) {
			if (!(this_present_reportName && that_present_reportName))
				return false;
			if (!this.reportName.equals(that.reportName))
				return false;
		}

		boolean this_present_paginationPageThrift = true && this.isSetPaginationPageThrift();
		boolean that_present_paginationPageThrift = true && that.isSetPaginationPageThrift();
		if (this_present_paginationPageThrift || that_present_paginationPageThrift) {
			if (!(this_present_paginationPageThrift && that_present_paginationPageThrift))
				return false;
			if (!this.paginationPageThrift.equals(that.paginationPageThrift))
				return false;
		}

		boolean this_present_generateReport = true;
		boolean that_present_generateReport = true;
		if (this_present_generateReport || that_present_generateReport) {
			if (!(this_present_generateReport && that_present_generateReport))
				return false;
			if (this.generateReport != that.generateReport)
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_reportingTypeRequest = true && (isSetReportingTypeRequest());
		builder.append(present_reportingTypeRequest);
		if (present_reportingTypeRequest)
			builder.append(reportingTypeRequest);

		boolean present_columnValuesRequests = true && (isSetColumnValuesRequests());
		builder.append(present_columnValuesRequests);
		if (present_columnValuesRequests)
			builder.append(columnValuesRequests);

		boolean present_reportName = true && (isSetReportName());
		builder.append(present_reportName);
		if (present_reportName)
			builder.append(reportName);

		boolean present_paginationPageThrift = true && (isSetPaginationPageThrift());
		builder.append(present_paginationPageThrift);
		if (present_paginationPageThrift)
			builder.append(paginationPageThrift);

		boolean present_generateReport = true;
		builder.append(present_generateReport);
		if (present_generateReport)
			builder.append(generateReport);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FilteringEntityRequest(");
		boolean first = true;

		sb.append("reportingTypeRequest:");
		if (this.reportingTypeRequest == null) {
			sb.append("null");
		} else {
			sb.append(this.reportingTypeRequest);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("columnValuesRequests:");
		if (this.columnValuesRequests == null) {
			sb.append("null");
		} else {
			sb.append(this.columnValuesRequests);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportName:");
		if (this.reportName == null) {
			sb.append("null");
		} else {
			sb.append(this.reportName);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("paginationPageThrift:");
		if (this.paginationPageThrift == null) {
			sb.append("null");
		} else {
			sb.append(this.paginationPageThrift);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("generateReport:");
		sb.append(this.generateReport);
		first = false;
		sb.append(")");
		return sb.toString();
	}
}