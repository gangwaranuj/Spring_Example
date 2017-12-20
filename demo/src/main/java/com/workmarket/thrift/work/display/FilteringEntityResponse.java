package com.workmarket.thrift.work.display;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FilteringEntityResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private HtmlTagTypeThrift htmlTagTypeThrift;
	private WorkReportEntityResponse workReportEntityResponse;
	private List<SelectOptionThrift> selectOptionThrifts;
	private com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThrift;
	private com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThriftOptional;
	private InputValues inputValues;
	private boolean isDisplay;
	private String displayName;
	private String toolTip;

	public FilteringEntityResponse() {
	}

	public FilteringEntityResponse(
			HtmlTagTypeThrift htmlTagTypeThrift,
			WorkReportEntityResponse workReportEntityResponse,
			List<SelectOptionThrift> selectOptionThrifts,
			com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThrift,
			com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThriftOptional,
			InputValues inputValues,
			boolean isDisplay,
			String toolTip) {
		this();
		this.htmlTagTypeThrift = htmlTagTypeThrift;
		this.workReportEntityResponse = workReportEntityResponse;
		this.selectOptionThrifts = selectOptionThrifts;
		this.relationalOperatorThrift = relationalOperatorThrift;
		this.relationalOperatorThriftOptional = relationalOperatorThriftOptional;
		this.inputValues = inputValues;
		this.isDisplay = isDisplay;
		this.toolTip = toolTip;
	}

	public HtmlTagTypeThrift getHtmlTagTypeThrift() {
		return this.htmlTagTypeThrift;
	}

	public FilteringEntityResponse setHtmlTagTypeThrift(HtmlTagTypeThrift htmlTagTypeThrift) {
		this.htmlTagTypeThrift = htmlTagTypeThrift;
		return this;
	}

	public boolean isSetHtmlTagTypeThrift() {
		return this.htmlTagTypeThrift != null;
	}

	public WorkReportEntityResponse getWorkReportEntityResponse() {
		return this.workReportEntityResponse;
	}

	public FilteringEntityResponse setWorkReportEntityResponse(WorkReportEntityResponse workReportEntityResponse) {
		this.workReportEntityResponse = workReportEntityResponse;
		return this;
	}

	public boolean isSetWorkReportEntityResponse() {
		return this.workReportEntityResponse != null;
	}

	public int getSelectOptionThriftsSize() {
		return (this.selectOptionThrifts == null) ? 0 : this.selectOptionThrifts.size();
	}

	public java.util.Iterator<SelectOptionThrift> getSelectOptionThriftsIterator() {
		return (this.selectOptionThrifts == null) ? null : this.selectOptionThrifts.iterator();
	}

	public void addToSelectOptionThrifts(SelectOptionThrift elem) {
		if (this.selectOptionThrifts == null) {
			this.selectOptionThrifts = new ArrayList<SelectOptionThrift>();
		}
		this.selectOptionThrifts.add(elem);
	}

	public List<SelectOptionThrift> getSelectOptionThrifts() {
		return this.selectOptionThrifts;
	}

	public FilteringEntityResponse setSelectOptionThrifts(List<SelectOptionThrift> selectOptionThrifts) {
		this.selectOptionThrifts = selectOptionThrifts;
		return this;
	}

	public boolean isSetSelectOptionThrifts() {
		return this.selectOptionThrifts != null;
	}

	public com.workmarket.thrift.work.report.RelationalOperatorThrift getRelationalOperatorThrift() {
		return this.relationalOperatorThrift;
	}

	public FilteringEntityResponse setRelationalOperatorThrift(com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThrift) {
		this.relationalOperatorThrift = relationalOperatorThrift;
		return this;
	}

	public boolean isSetRelationalOperatorThrift() {
		return this.relationalOperatorThrift != null;
	}

	public com.workmarket.thrift.work.report.RelationalOperatorThrift getRelationalOperatorThriftOptional() {
		return this.relationalOperatorThriftOptional;
	}

	public FilteringEntityResponse setRelationalOperatorThriftOptional(com.workmarket.thrift.work.report.RelationalOperatorThrift relationalOperatorThriftOptional) {
		this.relationalOperatorThriftOptional = relationalOperatorThriftOptional;
		return this;
	}

	public boolean isSetRelationalOperatorThriftOptional() {
		return this.relationalOperatorThriftOptional != null;
	}

	public InputValues getInputValues() {
		return this.inputValues;
	}

	public FilteringEntityResponse setInputValues(InputValues inputValues) {
		this.inputValues = inputValues;
		return this;
	}

	public boolean isSetInputValues() {
		return this.inputValues != null;
	}

	public boolean isIsDisplay() {
		return this.isDisplay;
	}

	public FilteringEntityResponse setIsDisplay(boolean isDisplay) {
		this.isDisplay = isDisplay;
		return this;
	}

	public String getToolTip() {
		return this.toolTip;
	}

	public FilteringEntityResponse setToolTip(String toolTip) {
		this.toolTip = toolTip;
		return this;
	}

	public boolean isSetToolTip() {
		return this.toolTip != null;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof FilteringEntityResponse)
			return this.equals((FilteringEntityResponse) that);
		return false;
	}

	private boolean equals(FilteringEntityResponse that) {
		if (that == null)
			return false;

		boolean this_present_htmlTagTypeThrift = true && this.isSetHtmlTagTypeThrift();
		boolean that_present_htmlTagTypeThrift = true && that.isSetHtmlTagTypeThrift();
		if (this_present_htmlTagTypeThrift || that_present_htmlTagTypeThrift) {
			if (!(this_present_htmlTagTypeThrift && that_present_htmlTagTypeThrift))
				return false;
			if (!this.htmlTagTypeThrift.equals(that.htmlTagTypeThrift))
				return false;
		}

		boolean this_present_workReportEntityResponse = true && this.isSetWorkReportEntityResponse();
		boolean that_present_workReportEntityResponse = true && that.isSetWorkReportEntityResponse();
		if (this_present_workReportEntityResponse || that_present_workReportEntityResponse) {
			if (!(this_present_workReportEntityResponse && that_present_workReportEntityResponse))
				return false;
			if (!this.workReportEntityResponse.equals(that.workReportEntityResponse))
				return false;
		}

		boolean this_present_selectOptionThrifts = true && this.isSetSelectOptionThrifts();
		boolean that_present_selectOptionThrifts = true && that.isSetSelectOptionThrifts();
		if (this_present_selectOptionThrifts || that_present_selectOptionThrifts) {
			if (!(this_present_selectOptionThrifts && that_present_selectOptionThrifts))
				return false;
			if (!this.selectOptionThrifts.equals(that.selectOptionThrifts))
				return false;
		}

		boolean this_present_relationalOperatorThrift = true && this.isSetRelationalOperatorThrift();
		boolean that_present_relationalOperatorThrift = true && that.isSetRelationalOperatorThrift();
		if (this_present_relationalOperatorThrift || that_present_relationalOperatorThrift) {
			if (!(this_present_relationalOperatorThrift && that_present_relationalOperatorThrift))
				return false;
			if (!this.relationalOperatorThrift.equals(that.relationalOperatorThrift))
				return false;
		}

		boolean this_present_relationalOperatorThriftOptional = true && this.isSetRelationalOperatorThriftOptional();
		boolean that_present_relationalOperatorThriftOptional = true && that.isSetRelationalOperatorThriftOptional();
		if (this_present_relationalOperatorThriftOptional || that_present_relationalOperatorThriftOptional) {
			if (!(this_present_relationalOperatorThriftOptional && that_present_relationalOperatorThriftOptional))
				return false;
			if (!this.relationalOperatorThriftOptional.equals(that.relationalOperatorThriftOptional))
				return false;
		}

		boolean this_present_inputValues = true && this.isSetInputValues();
		boolean that_present_inputValues = true && that.isSetInputValues();
		if (this_present_inputValues || that_present_inputValues) {
			if (!(this_present_inputValues && that_present_inputValues))
				return false;
			if (!this.inputValues.equals(that.inputValues))
				return false;
		}

		boolean this_present_isDisplay = true;
		boolean that_present_isDisplay = true;
		if (this_present_isDisplay || that_present_isDisplay) {
			if (!(this_present_isDisplay && that_present_isDisplay))
				return false;
			if (this.isDisplay != that.isDisplay)
				return false;
		}

		boolean this_present_toolTip = true && this.isSetToolTip();
		boolean that_present_toolTip = true && that.isSetToolTip();
		if (this_present_toolTip || that_present_toolTip) {
			if (!(this_present_toolTip && that_present_toolTip))
				return false;
			if (!this.toolTip.equals(that.toolTip))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_htmlTagTypeThrift = true && (isSetHtmlTagTypeThrift());
		builder.append(present_htmlTagTypeThrift);
		if (present_htmlTagTypeThrift)
			builder.append(htmlTagTypeThrift.getValue());

		boolean present_workReportEntityResponse = true && (isSetWorkReportEntityResponse());
		builder.append(present_workReportEntityResponse);
		if (present_workReportEntityResponse)
			builder.append(workReportEntityResponse);

		boolean present_selectOptionThrifts = true && (isSetSelectOptionThrifts());
		builder.append(present_selectOptionThrifts);
		if (present_selectOptionThrifts)
			builder.append(selectOptionThrifts);

		boolean present_relationalOperatorThrift = true && (isSetRelationalOperatorThrift());
		builder.append(present_relationalOperatorThrift);
		if (present_relationalOperatorThrift)
			builder.append(relationalOperatorThrift.getValue());

		boolean present_relationalOperatorThriftOptional = true && (isSetRelationalOperatorThriftOptional());
		builder.append(present_relationalOperatorThriftOptional);
		if (present_relationalOperatorThriftOptional)
			builder.append(relationalOperatorThriftOptional.getValue());

		boolean present_inputValues = true && (isSetInputValues());
		builder.append(present_inputValues);
		if (present_inputValues)
			builder.append(inputValues);

		boolean present_isDisplay = true;
		builder.append(present_isDisplay);
		if (present_isDisplay)
			builder.append(isDisplay);

		boolean present_toolTip = true && (isSetToolTip());
		builder.append(present_toolTip);
		if (present_toolTip)
			builder.append(toolTip);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("FilteringEntityResponse(");
		boolean first = true;

		sb.append("htmlTagTypeThrift:");
		if (this.htmlTagTypeThrift == null) {
			sb.append("null");
		} else {
			sb.append(this.htmlTagTypeThrift);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("workReportEntityResponse:");
		if (this.workReportEntityResponse == null) {
			sb.append("null");
		} else {
			sb.append(this.workReportEntityResponse);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("selectOptionThrifts:");
		if (this.selectOptionThrifts == null) {
			sb.append("null");
		} else {
			sb.append(this.selectOptionThrifts);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("relationalOperatorThrift:");
		if (this.relationalOperatorThrift == null) {
			sb.append("null");
		} else {
			sb.append(this.relationalOperatorThrift);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("relationalOperatorThriftOptional:");
		if (this.relationalOperatorThriftOptional == null) {
			sb.append("null");
		} else {
			sb.append(this.relationalOperatorThriftOptional);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("inputValues:");
		if (this.inputValues == null) {
			sb.append("null");
		} else {
			sb.append(this.inputValues);
		}
		first = false;
		if (!first) sb.append(", ");
		sb.append("isDisplay:");
		sb.append(this.isDisplay);
		first = false;
		if (!first) sb.append(", ");
		sb.append("toolTip:");
		if (this.toolTip == null) {
			sb.append("null");
		} else {
			sb.append(this.toolTip);
		}
		first = false;
		sb.append(")");
		return sb.toString();
	}
}