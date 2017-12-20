package com.workmarket.service.business.event;

import java.util.List;

/**
 * place holder.
 */
public class InviteToGroupFromRecommendationEvent extends InviteToGroupEvent {
	public InviteToGroupFromRecommendationEvent(List<Long> inviteeUserIds, Long groupId, Long invitedByUserId) {
		super(inviteeUserIds, groupId, invitedByUserId);
	}
}
