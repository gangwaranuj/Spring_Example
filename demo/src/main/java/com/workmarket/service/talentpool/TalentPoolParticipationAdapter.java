package com.workmarket.service.talentpool;

import com.google.common.base.Optional;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipation;
import com.workmarket.domains.groups.model.GroupMemberRequestType;

public interface TalentPoolParticipationAdapter {

	Optional<GroupMemberRequestType> getGroupMemberRequestType(final TalentPoolParticipation talentPoolParticipation);

}
