package com.workmarket.search.response.work;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;

public class DashboardResponse extends WorkSearchResponse {

	private static final long serialVersionUID = -3418395771810499110L;

	public DashboardResponse() {}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof DashboardResponse)) {
			return false;
		}

		DashboardResponse that = (DashboardResponse) obj;
		return new EqualsBuilder()
			.append(getSidebar(), that.getSidebar())
			.append(getDashboardResultList(), that.getDashboardResultList())
			.isEquals();
	}

	@Override
	public final int hashCode() {
		return new HashCodeBuilder(17, 37)
			.append(getSidebar())
			.append(getDashboardResultList())
			.toHashCode();
	}
}
