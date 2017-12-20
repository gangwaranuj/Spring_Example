package com.workmarket.reporting.util;

import com.workmarket.domains.model.reporting.ReportingContext;

public class ReportingContextComparator implements java.util.Comparator<ReportingContext> {

	@Override
	public int compare(ReportingContext o1, ReportingContext o2) {
		if(o1.getSortOrder() < o2.getSortOrder())
			return 0;
		else 
			return 1;
	}

}