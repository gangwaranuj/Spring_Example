package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.aop.target.LazyInitTargetSource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReportResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String reportUri;
	private ReportRow reportHeader;
	private List<ReportRow> reportRow;
	private WorkReportEntityBucketsCompositeResponse workReportEntityBucketsCompositeResponse;
	private PaginationThrift paginationThrift;

	public ReportResponse() {
	}

	public ReportResponse(
			String reportUri,
			ReportRow reportHeader,
			List<ReportRow> reportRow,
			WorkReportEntityBucketsCompositeResponse workReportEntityBucketsCompositeResponse,
			PaginationThrift paginationThrift) {
		this.reportUri = reportUri;
		this.reportHeader = reportHeader;
		this.reportRow = reportRow;
		this.workReportEntityBucketsCompositeResponse = workReportEntityBucketsCompositeResponse;
		this.paginationThrift = paginationThrift;
	}

	public ReportResponse(String reportUri,ReportRow reportHeader,List<ReportRow> reportRow){
		this.reportUri = reportUri;
		this.reportHeader = reportHeader;
		this.reportRow = reportRow;
	}

    public ReportResponse(
            String reportUri,
            List<ReportRow> reportRow) {
        this.reportUri = reportUri;
        this.reportHeader = reportHeader;
        this.reportRow = reportRow;
    }

	public String getReportUri() {
		return this.reportUri;
	}

	public ReportResponse setReportUri(String reportUri) {
		this.reportUri = reportUri;
		return this;
	}

	public boolean isSetReportUri() {
		return this.reportUri != null;
	}

	public ReportRow getReportHeader() {
		return this.reportHeader;
	}

	public ReportResponse setReportHeader(ReportRow reportHeader) {
		this.reportHeader = reportHeader;
		return this;
	}

	public boolean isSetReportHeader() {
		return this.reportHeader != null;
	}

	public int getReportRowSize() {
		return (this.reportRow == null) ? 0 : this.reportRow.size();
	}

	public java.util.Iterator<ReportRow> getReportRowIterator() {
		return (this.reportRow == null) ? null : this.reportRow.iterator();
	}

	public void addToReportRow(ReportRow elem) {
		if (this.reportRow == null) {
			this.reportRow = new ArrayList<ReportRow>();
		}
		this.reportRow.add(elem);
	}

	public List<ReportRow> getReportRow() {
		return this.reportRow;
	}

	public ReportResponse setReportRow(List<ReportRow> reportRow) {
		this.reportRow = reportRow;
		return this;
	}

	public boolean isSetReportRow() {
		return this.reportRow != null;
	}

	public WorkReportEntityBucketsCompositeResponse getWorkReportEntityBucketsCompositeResponse() {
		return this.workReportEntityBucketsCompositeResponse;
	}

	public ReportResponse setWorkReportEntityBucketsCompositeResponse(WorkReportEntityBucketsCompositeResponse workReportEntityBucketsCompositeResponse) {
		this.workReportEntityBucketsCompositeResponse = workReportEntityBucketsCompositeResponse;
		return this;
	}

	public boolean isSetWorkReportEntityBucketsCompositeResponse() {
		return this.workReportEntityBucketsCompositeResponse != null;
	}

	public PaginationThrift getPaginationThrift() {
		return this.paginationThrift;
	}

	public ReportResponse setPaginationThrift(PaginationThrift paginationThrift) {
		this.paginationThrift = paginationThrift;
		return this;
	}

	public boolean isSetPaginationThrift() {
		return this.paginationThrift != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof ReportResponse)
			return this.equals((ReportResponse) that);
		return false;
	}

	private boolean equals(ReportResponse that) {
		if (that == null)
			return false;

		boolean this_present_reportUri = true && this.isSetReportUri();
		boolean that_present_reportUri = true && that.isSetReportUri();
		if (this_present_reportUri || that_present_reportUri) {
			if (!(this_present_reportUri && that_present_reportUri))
				return false;
			if (!this.reportUri.equals(that.reportUri))
				return false;
		}

		boolean this_present_reportHeader = true && this.isSetReportHeader();
		boolean that_present_reportHeader = true && that.isSetReportHeader();
		if (this_present_reportHeader || that_present_reportHeader) {
			if (!(this_present_reportHeader && that_present_reportHeader))
				return false;
			if (!this.reportHeader.equals(that.reportHeader))
				return false;
		}

		boolean this_present_reportRow = true && this.isSetReportRow();
		boolean that_present_reportRow = true && that.isSetReportRow();
		if (this_present_reportRow || that_present_reportRow) {
			if (!(this_present_reportRow && that_present_reportRow))
				return false;
			if (!this.reportRow.equals(that.reportRow))
				return false;
		}

		boolean this_present_workReportEntityBucketsCompositeResponse = true && this.isSetWorkReportEntityBucketsCompositeResponse();
		boolean that_present_workReportEntityBucketsCompositeResponse = true && that.isSetWorkReportEntityBucketsCompositeResponse();
		if (this_present_workReportEntityBucketsCompositeResponse || that_present_workReportEntityBucketsCompositeResponse) {
			if (!(this_present_workReportEntityBucketsCompositeResponse && that_present_workReportEntityBucketsCompositeResponse))
				return false;
			if (!this.workReportEntityBucketsCompositeResponse.equals(that.workReportEntityBucketsCompositeResponse))
				return false;
		}

		boolean this_present_paginationThrift = true && this.isSetPaginationThrift();
		boolean that_present_paginationThrift = true && that.isSetPaginationThrift();
		if (this_present_paginationThrift || that_present_paginationThrift) {
			if (!(this_present_paginationThrift && that_present_paginationThrift))
				return false;
			if (!this.paginationThrift.equals(that.paginationThrift))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_reportUri = true && (isSetReportUri());
		builder.append(present_reportUri);
		if (present_reportUri)
			builder.append(reportUri);

		boolean present_reportHeader = true && (isSetReportHeader());
		builder.append(present_reportHeader);
		if (present_reportHeader)
			builder.append(reportHeader);

		boolean present_reportRow = true && (isSetReportRow());
		builder.append(present_reportRow);
		if (present_reportRow)
			builder.append(reportRow);

		boolean present_workReportEntityBucketsCompositeResponse = true && (isSetWorkReportEntityBucketsCompositeResponse());
		builder.append(present_workReportEntityBucketsCompositeResponse);
		if (present_workReportEntityBucketsCompositeResponse)
			builder.append(workReportEntityBucketsCompositeResponse);

		boolean present_paginationThrift = true && (isSetPaginationThrift());
		builder.append(present_paginationThrift);
		if (present_paginationThrift)
			builder.append(paginationThrift);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("ReportResponse(");
		boolean first = true;

		sb.append("reportUri:");
		if (this.reportUri == null) {
			sb.append("null");
		} else {
			sb.append(this.reportUri);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportHeader:");
		if (this.reportHeader == null) {
			sb.append("null");
		} else {
			sb.append(this.reportHeader);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("reportRow:");
		if (this.reportRow == null) {
			sb.append("null");
		} else {
			sb.append(this.reportRow);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("workReportEntityBucketsCompositeResponse:");
		if (this.workReportEntityBucketsCompositeResponse == null) {
			sb.append("null");
		} else {
			sb.append(this.workReportEntityBucketsCompositeResponse);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("paginationThrift:");
		if (this.paginationThrift == null) {
			sb.append("null");
		} else {
			sb.append(this.paginationThrift);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}

}