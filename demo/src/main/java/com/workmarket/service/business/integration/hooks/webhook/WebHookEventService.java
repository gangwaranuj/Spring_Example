package com.workmarket.service.business.integration.hooks.webhook;

import com.workmarket.domains.work.model.negotiation.AbstractWorkNegotiation;

import java.math.BigDecimal;

public interface WebHookEventService {

	public void onWorkCreated(String workNumber, String autotaskId);
	public void onWorkCreated(Long workId, String autotaskId);
	public void onWorkCreated(Long workId, Long companyId, Long autotaskId);
	public void onWorkAccepted(Long workId, Long companyId, Long resourceId);
	public void onNoteAdded(Long workId, Long companyId, Long noteId);
	public void onAssetAdded(Long workId, Long companyId, Long assetId);
	public void onCheckInActiveResource(Long workId, Long companyId, Long timeTrackingId);
	public void onCheckOutActiveResource(Long workId, Long companyId, Long timeTrackingId);
	public void onWorkCompleted(Long workId, Long companyId);
	public void onWorkApproved(Long workId, Long companyId);

	public void onWorkCustomFieldsUpdated(Long workId, Long companyId);

	public void onWorkSent(Long workId, Long companyId);
	public void onWorkVoided(Long workId, Long companyId);
	public void onWorkCancelled(Long workId, Long companyId);
	public void onWorkPaid(Long workId, Long companyId);
	public void onWorkConfirmed(Long workId, Long companyId);
	public void onAssetRemoved(Long workId, Long assetId);
	public void onLabelAdded(Long workId, Long companyId, Long workSubStatusTypeAssociationId);
	public void onLabelRemoved(Long workId, Long companyId, Long workSubStatusTypeAssociationId);
	public void onNegotiationAdded(Long workId, Long companyId, AbstractWorkNegotiation negotiation);
	public void onNegotiationRequested(Long workId, Long companyId, AbstractWorkNegotiation negotiation);
	public void onNegotiationApproved(Long workId, Long companyId, AbstractWorkNegotiation negotiation, BigDecimal amount);
	public void onNegotiationDeclined(Long workId, Long companyId, AbstractWorkNegotiation negotiation);
}
