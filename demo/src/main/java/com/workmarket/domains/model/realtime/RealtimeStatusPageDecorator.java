package com.workmarket.domains.model.realtime;

import com.workmarket.thrift.services.realtime.RealtimeRow;
import com.workmarket.thrift.services.realtime.RealtimeStatusPage;

public class RealtimeStatusPageDecorator extends RealtimeStatusPage implements IRealtimeStatusPage {

	private static final long serialVersionUID = -161984430571965307L;

	public RealtimeStatusPageDecorator() {
		super();
	}

	public void addToRows(IRealtimeRow row) {
		this.addToRows((RealtimeRow) row);
	}

	@Override
	public void setNumberOfResults(long numOfResults) {
		this.setNumResults(numOfResults);
	}


}
