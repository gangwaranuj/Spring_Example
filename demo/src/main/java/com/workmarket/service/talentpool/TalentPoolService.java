package com.workmarket.service.talentpool;

import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;

import java.util.List;
import java.util.Map;

public interface TalentPoolService {

	Map<Long, String> getVendorGroupMemberRequestTypeStatuses(long userGroupId, List<Long> companyIds);

	boolean isAlreadyParticipatingInTalentPool(String talentPoolUuid, String vendorUuid);

	Map<String, List<TalentPoolParticipant>> getTalentPoolAndParticipants(List<String> talentPoolUuids);
}
