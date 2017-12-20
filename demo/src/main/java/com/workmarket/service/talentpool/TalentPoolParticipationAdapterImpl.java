package com.workmarket.service.talentpool;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.domains.groups.model.GroupMemberRequestType;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

@Component
public class TalentPoolParticipationAdapterImpl implements TalentPoolParticipationAdapter {

	enum ParticipationProperty {
		INVITED_ON, APPLIED_ON, APPROVED_ON, DECLINED_ON, OVERRIDE_GRANTED_ON;
	}

	@Override
	public Optional<GroupMemberRequestType> getGroupMemberRequestType(final TalentPoolParticipation talentPoolParticipation) {
		final ImmutableSet<ParticipationProperty> properties = getSetProperties(talentPoolParticipation);

		if (properties.containsAll(ImmutableSet.of(ParticipationProperty.OVERRIDE_GRANTED_ON, ParticipationProperty.APPROVED_ON))) {
			return Optional.of(GroupMemberRequestType.MEMBER_OVERRIDE);
		}

		if (properties.contains(ParticipationProperty.APPROVED_ON)) {
			return Optional.of(GroupMemberRequestType.MEMBER_PASSED);
		}

		if (properties.containsAll(ImmutableSet.of(ParticipationProperty.DECLINED_ON, ParticipationProperty.APPLIED_ON))) {
			return Optional.of(GroupMemberRequestType.DECLINED);
		}

		if (properties.contains(ParticipationProperty.APPLIED_ON)) {
			return Optional.of(GroupMemberRequestType.PENDING_PASSED);
		}

		if (properties.contains(ParticipationProperty.INVITED_ON)) {
			return Optional.of(GroupMemberRequestType.INVITED);
		}

		return Optional.absent();
	}

	@VisibleForTesting
	protected ImmutableSet<ParticipationProperty> getSetProperties(final TalentPoolParticipation talentPoolParticipation) {
		final ImmutableSet.Builder<ParticipationProperty> setProperties = ImmutableSet.builder();
		if (StringUtils.isNotBlank(talentPoolParticipation.getInvitedOn())) {
			setProperties.add(ParticipationProperty.INVITED_ON);
		}
		if (StringUtils.isNotBlank(talentPoolParticipation.getAppliedOn())) {
			setProperties.add(ParticipationProperty.APPLIED_ON);
		}
		if (StringUtils.isNotBlank(talentPoolParticipation.getApprovedOn())) {
			setProperties.add(ParticipationProperty.APPROVED_ON);
		}
		if (StringUtils.isNotBlank(talentPoolParticipation.getDeclinedOn())) {
			setProperties.add(ParticipationProperty.DECLINED_ON);
		}
		if (StringUtils.isNotBlank(talentPoolParticipation.getOverrideGrantedOn())) {
			setProperties.add(ParticipationProperty.OVERRIDE_GRANTED_ON);
		}
		return setProperties.build();
	}

}

