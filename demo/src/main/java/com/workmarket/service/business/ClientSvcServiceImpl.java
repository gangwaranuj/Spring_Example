package com.workmarket.service.business;

import com.workmarket.dao.UserDAO;
import com.workmarket.domains.work.dao.WorkDAO;
import com.workmarket.dao.note.NoteDAO;
import com.workmarket.dao.note.concern.ConcernDAO;
import com.workmarket.domains.model.Invitation;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.assessment.AbstractAssessment;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.note.concern.AssessmentConcern;
import com.workmarket.domains.model.note.concern.Concern;
import com.workmarket.domains.model.note.concern.ConcernPagination;
import com.workmarket.domains.model.note.concern.InvitationConcern;
import com.workmarket.domains.model.note.concern.ProfileConcern;
import com.workmarket.domains.model.note.concern.RecruitingCampaignConcern;
import com.workmarket.domains.model.note.concern.UserGroupConcern;
import com.workmarket.domains.model.note.concern.WorkConcern;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.work.model.Work;
import com.workmarket.service.business.dto.ClientSvcDashboardDTO;
import com.workmarket.service.business.dto.RegistrationConcernDTO;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.utility.BeanUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Service
public class ClientSvcServiceImpl implements ClientSvcService {

	@Autowired private UserDAO userDAO;
	@Autowired private WorkDAO workDAO;
	@Autowired private ConcernDAO concernDAO;
	@Autowired private UserGroupService userGroupService;
	@Autowired private NoteDAO noteDAO;
	@Autowired private InvitationService invitationService;
	@Autowired private RecruitingService recruitingService;
	@Autowired private AssessmentService assessmentService;
	@Autowired private AuthenticationService authenticationService;

	@Override
	public ClientSvcDashboardDTO getDashboard() {

		ClientSvcDashboardDTO dashboard = new ClientSvcDashboardDTO();
		dashboard.setNewUsers(userDAO.countAllPendingUsers());
		dashboard.setPendingProfileUpdates(userDAO.countAllProfilesPendingApproval());
		dashboard.setActiveWork(workDAO.countAllActiveWork());
		dashboard.setConcerns(concernDAO.countConcerns());
		return dashboard;

	}

	@Override
	public Concern reportUserGroup(Long userGroupId, String message) {
		Assert.notNull(userGroupId);
		Assert.hasText(message);
		UserGroup group = userGroupService.findGroupById(userGroupId);
		Assert.notNull(group, "Unable to find user group");
		UserGroupConcern concern = new UserGroupConcern(message, group);
		noteDAO.saveOrUpdate(concern);

		return concern;
	}

	@Override
	public Concern reportWork(Long workId, String message) {
		Assert.notNull(workId);
		Assert.hasText(message);
		Work work = workDAO.get(workId);
		Assert.notNull(work, "Unable to find assignment");
		WorkConcern concern = new WorkConcern(message, work);
		noteDAO.saveOrUpdate(concern);

		return concern;
	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public Concern reportProfile(Long userId, String message) {
		Assert.notNull(userId);
		Assert.hasText(message);
		User user = userDAO.get(userId);
		Assert.notNull(user, "Unable to find user");

		ProfileConcern concern = new ProfileConcern(message, user);
		noteDAO.saveOrUpdate(concern);

		return concern;
	}

	@Override
	public Concern reportInvitation(Long invitationId, RegistrationConcernDTO dto) {
		Assert.notNull(invitationId);
		Assert.hasText(dto.getMessage());
		Invitation invitation = invitationService.findInvitationById(invitationId);
		Assert.notNull(invitation, "Unable to find invitation");

		InvitationConcern concern = new InvitationConcern(dto.getMessage(), invitation);
		BeanUtilities.copyProperties(concern, dto);
		noteDAO.saveOrUpdate(concern);


		return concern;
	}

	@Override
	public Concern reportRecruitingCampaign(Long companyId, Long campaignId, RegistrationConcernDTO dto) {
		Assert.notNull(campaignId);
		Assert.hasText(dto.getMessage());
		RecruitingCampaign campaign = recruitingService.findRecruitingCampaign(companyId ,campaignId);
		Assert.notNull(campaign, "Unable to find recruiting campaign");
		RecruitingCampaignConcern concern = new RecruitingCampaignConcern(dto.getMessage(), campaign);

		BeanUtilities.copyProperties(concern, dto);
		noteDAO.saveOrUpdate(concern);

		return concern;
	}

	@Override
	public Concern reportAssessment(Long assessmentId, String message) {
		Assert.notNull(assessmentId);
		Assert.hasText(message);
		AbstractAssessment assessment = assessmentService.findAssessment(assessmentId);
		Assert.notNull(assessment, "Unable to find assessment");
		AssessmentConcern concern = new AssessmentConcern(message, assessment);
		noteDAO.saveOrUpdate(concern);

		return concern;
	}

	@Override
	public Concern findConcernById(Long concernId) {
		Assert.notNull(concernId);
		return noteDAO.findById(Concern.class, concernId);
	}

	@Override
	public ConcernPagination findAllConcerns(ConcernPagination pagination) {
		return noteDAO.findAllConcerns(Concern.class, pagination);
	}

	@Override
	public Concern updateConcernResolvedStatus(Long concernId, boolean status) {
		Assert.notNull(concernId);
		Assert.notNull(status);
		Concern concern = noteDAO.findById(Concern.class, concernId);
		Assert.notNull(concern);
		concern.setResolved(status);
		concern.setResolvedBy(authenticationService.getCurrentUser());

		return concern;
	}

	@Override
	public Concern deleteConcern(Long concernId) {
		Assert.notNull(concernId);

		Concern concern = noteDAO.findById(Concern.class, concernId);
		Assert.notNull(concern);
		concern.setDeleted(true);

		return concern;
	}
}

