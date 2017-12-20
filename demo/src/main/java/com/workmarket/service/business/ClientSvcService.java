package com.workmarket.service.business;

import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ConcernPagination;
import com.workmarket.service.business.dto.ClientSvcDashboardDTO;
import com.workmarket.service.business.dto.RegistrationConcernDTO;

public interface ClientSvcService {

	ClientSvcDashboardDTO getDashboard();

	Concern reportUserGroup(Long userGroupId, String message);

	Concern reportWork(Long workId, String message);

	Concern reportProfile(Long userId, String message);

	Concern reportInvitation(Long invitationId, RegistrationConcernDTO dto);

	Concern reportRecruitingCampaign(Long companyId, Long campaignId, RegistrationConcernDTO dto);

	Concern reportAssessment(Long assessmentId, String message);

	Concern findConcernById(Long concernId);

	ConcernPagination findAllConcerns(ConcernPagination pagination);

	Concern updateConcernResolvedStatus(Long concernId, boolean status);

	Concern deleteConcern(Long concernId);
}