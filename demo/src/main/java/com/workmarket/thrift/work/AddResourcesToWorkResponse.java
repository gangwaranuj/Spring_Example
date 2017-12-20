package com.workmarket.thrift.work;

import com.workmarket.domains.work.service.resource.WorkAuthorizationResponse;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.Map;
import java.util.Set;

public class AddResourcesToWorkResponse implements Serializable {
	private static final long serialVersionUID = 1L;

	private String workNumber;
	private Map<WorkAuthorizationResponse, Set<String>> userMap;

	public AddResourcesToWorkResponse() {
	}

	public AddResourcesToWorkResponse(String workNumber) {
		this();
		this.workNumber = workNumber;
	}

	public String getWorkNumber() {
		return this.workNumber;
	}

	public AddResourcesToWorkResponse setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
		return this;
	}

	public boolean isSetWorkNumber() {
		return this.workNumber != null;
	}

	public int getUserMapSize() {
		return (this.userMap == null) ? 0 : this.userMap.size();
	}

	public Map<WorkAuthorizationResponse, Set<String>> getUserMap() {
		return this.userMap;
	}

	public AddResourcesToWorkResponse setUserMap(Map<WorkAuthorizationResponse, Set<String>> userMap) {
		this.userMap = userMap;
		return this;
	}

	public boolean isSetUserMap() {
		return this.userMap != null;
	}

	@Override
	public boolean equals(Object that) {
		if (that == null)
			return false;
		if (that instanceof AddResourcesToWorkResponse)
			return this.equals((AddResourcesToWorkResponse) that);
		return false;
	}

	private boolean equals(AddResourcesToWorkResponse that) {
		if (that == null)
			return false;

		boolean this_present_workNumber = true && this.isSetWorkNumber();
		boolean that_present_workNumber = true && that.isSetWorkNumber();
		if (this_present_workNumber || that_present_workNumber) {
			if (!(this_present_workNumber && that_present_workNumber))
				return false;
			if (!this.workNumber.equals(that.workNumber))
				return false;
		}

		boolean this_present_userMap = true && this.isSetUserMap();
		boolean that_present_userMap = true && that.isSetUserMap();
		if (this_present_userMap || that_present_userMap) {
			if (!(this_present_userMap && that_present_userMap))
				return false;
			if (!this.userMap.equals(that.userMap))
				return false;
		}

		return true;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder builder = new HashCodeBuilder();

		boolean present_workNumber = true && (isSetWorkNumber());
		builder.append(present_workNumber);
		if (present_workNumber)
			builder.append(workNumber);

		boolean present_userMap = true && (isSetUserMap());
		builder.append(present_userMap);
		if (present_userMap)
			builder.append(userMap);

		return builder.toHashCode();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("AddResourcesToWorkResponse(");
		boolean first = true;

		sb.append("workNumber:");
		if (this.workNumber == null) {
			sb.append("null");
		} else {
			sb.append(this.workNumber);
		}
		first = false;
		if (isSetUserMap()) {
			if (!first) sb.append(", ");
			sb.append("userMap:");
			if (this.userMap == null) {
				sb.append("null");
			} else {
				sb.append(this.userMap);
			}
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}
}