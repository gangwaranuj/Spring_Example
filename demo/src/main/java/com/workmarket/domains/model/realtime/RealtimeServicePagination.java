package com.workmarket.domains.model.realtime;

import com.workmarket.thrift.ServicePagination;
import com.workmarket.thrift.services.realtime.SortByType;

import java.util.List;

public class RealtimeServicePagination extends ServicePagination<SortByType> {

	private List<String> internalOwnerFilter;

	public void setInternalOwnerFilter(List<String> internalOwnerFilter) {
		this.internalOwnerFilter = internalOwnerFilter;
	}

	public List<String> getInternalOwnerFilter() {
		return internalOwnerFilter;
	}

	public boolean isSetInternalOwnerFilter() {
		return !(internalOwnerFilter == null || internalOwnerFilter.size() == 0);
	}
}
