package com.workmarket.service.business.event.asset;

import com.workmarket.service.business.event.Event;

public class DeleteDeliverableEvent extends Event {

	private Long workId;
	private Long deliverableRequirementId;
	private Integer position;

	public DeleteDeliverableEvent(Long workId, Long deliverableRequirementId, Integer position) {
		this.workId = workId;
		this.deliverableRequirementId = deliverableRequirementId;
		this.position = position;
	}

	public Long getWorkId() {
		return workId;
	}

	public void setWorkId(Long workId) {
		this.workId = workId;
	}

	public Long getDeliverableRequirementId() {
		return deliverableRequirementId;
	}

	public void setDeliverableRequirementId(Long deliverableRequirementId) {
		this.deliverableRequirementId = deliverableRequirementId;
	}

	public Integer getPosition() {
		return position;
	}

	public void setPosition(Integer position) {
		this.position = position;
	}

	@Override
	public String toString() {
		return "DeleteDeliverablesAtPositionEvent{" +
			"workId=" + workId +
			", deliverableRequirementId=" + deliverableRequirementId +
			", position=" + position +
			'}';
	}
}
