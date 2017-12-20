package com.workmarket.service.business.event.work;

import com.workmarket.service.business.event.Event;

public class WorkBundleDeclineOfferEvent extends Event {
	private static final long serialVersionUID = -2146642439466438956L;
	private Long negotiationId;
	private String workNumber;
	private String note;

	public WorkBundleDeclineOfferEvent(String workNumber, Long negotiationId, String note) {
		this.workNumber = workNumber;
		this.negotiationId = negotiationId;
		this.note = note;
	}

	public WorkBundleDeclineOfferEvent() {
	}

	public Long getNegotiationId() {
		return negotiationId;
	}

	public void setNegotiationId(Long negotiationId) {
		this.negotiationId = negotiationId;
	}

	public String getWorkNumber() {
		return workNumber;
	}

	public void setWorkNumber(String workNumber) {
		this.workNumber = workNumber;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
}
