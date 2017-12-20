package com.workmarket.thrift.work;

import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;

public class WorkActionResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private WorkActionResponseCodeType responseCode;
	private String message;

	public static WorkActionResponse success() {
		return new WorkActionResponse();
	}

	public WorkActionResponse() {
		this.responseCode = com.workmarket.thrift.work.WorkActionResponseCodeType.SUCCESS;
	}

	public WorkActionResponse(WorkActionResponseCodeType responseCode) {
		this();
		this.responseCode = responseCode;
	}

	public WorkActionResponseCodeType getResponseCode() {
		return this.responseCode;
	}

	public WorkActionResponse setResponseCode(WorkActionResponseCodeType responseCode) {
		this.responseCode = responseCode;
		return this;
	}

	public boolean isSetResponseCode() {
		return this.responseCode != null;
	}

	public String getMessage() {
		return this.message;
	}

	public WorkActionResponse setMessage(String message) {
		this.message = message;
		return this;
	}

	public boolean isSetMessage() {
		return this.message != null;
	}

	public boolean isSuccessful() {
		return WorkActionResponseCodeType.SUCCESS.equals(this.getResponseCode());
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof WorkActionResponse)
			return this.equals((WorkActionResponse) that);
		return false;
	}

	private boolean equals(WorkActionResponse that) {
		if (that == null)
			return false;

		boolean this_present_responseCode = true && this.isSetResponseCode();
		boolean that_present_responseCode = true && that.isSetResponseCode();
		if (this_present_responseCode || that_present_responseCode) {
			if (!(this_present_responseCode && that_present_responseCode))
				return false;
			if (!this.responseCode.equals(that.responseCode))
				return false;
		}

		boolean this_present_message = true && this.isSetMessage();
		boolean that_present_message = true && that.isSetMessage();
		if (this_present_message || that_present_message) {
			if (!(this_present_message && that_present_message))
				return false;
			if (!this.message.equals(that.message))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_responseCode = true && (isSetResponseCode());
		builder.append(present_responseCode);
		if (present_responseCode)
			builder.append(responseCode.getValue());

		boolean present_message = true && (isSetMessage());
		builder.append(present_message);
		if (present_message)
			builder.append(message);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("WorkActionResponse(");
		boolean first = true;

		sb.append("responseCode:");
		if (this.responseCode == null) {
			sb.append("null");
		} else {
			sb.append(this.responseCode);
		}
		first = false;
		if (isSetMessage()) {
			if (!first) sb.append(", ");
			sb.append("message:");
			if (this.message == null) {
				sb.append("null");
			} else {
				sb.append(this.message);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}