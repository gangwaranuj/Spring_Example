package com.workmarket.service.business;

import com.google.common.collect.Lists;
import com.workmarket.dao.UserDAO;
import com.workmarket.dao.company.CompanyDAO;
import com.workmarket.dao.recruiting.RecruitingCampaignDAO;
import com.workmarket.domains.groups.model.UserGroup;
import com.workmarket.domains.model.ApprovalStatus;
import com.workmarket.domains.model.User;
import com.workmarket.domains.model.lane.LaneAssociation;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;
import com.workmarket.domains.model.recruiting.RecruitingVendor;
import com.workmarket.dto.RecruitingCampaignUserPagination;
import com.workmarket.service.business.dto.RecruitingCampaignDTO;
import com.workmarket.service.infra.URIService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.service.infra.security.RequestContext;
import com.workmarket.utility.BeanUtilities;
import com.workmarket.utility.EncryptionUtilities;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.List;
import java.util.Random;

@Service
public class RecruitingServiceImpl implements RecruitingService {

	@Autowired private UserGroupService userGroupService;
	@Autowired private RecruitingCampaignDAO recruitingCampaignDAO;
	@Autowired private ProfileService profileService;
	@Autowired private URIService uriService;
	@Autowired private UserDAO userDAO;
	@Autowired private CompanyDAO companyDAO;
	@Autowired private AssetService assetService;
	@Autowired private UserService userService;
	@Autowired private LaneService laneService;
	@Autowired private RequestService requestService;
	@Autowired private AuthenticationService authenticationService;

	@Override
	public RecruitingCampaign saveOrUpdateRecruitingCampaign(RecruitingCampaignDTO recruitingCampaignDTO) {
		Assert.notNull(recruitingCampaignDTO);
		Assert.notNull(recruitingCampaignDTO.getCompanyId(), "Company Id can't be null");

		RecruitingCampaign recruitingCampaign;
		if (recruitingCampaignDTO.getRecruitingCampaignId() == null) {
			recruitingCampaign = BeanUtilities.newBean(RecruitingCampaign.class, recruitingCampaignDTO);

			if (recruitingCampaignDTO.getCompanyUserGroupId() != null) {
				recruitingCampaign.setCompanyUserGroup(userGroupService.findGroupById(recruitingCampaignDTO.getCompanyUserGroupId()));
			}
		} else {
			recruitingCampaign = recruitingCampaignDAO.get(recruitingCampaignDTO.getRecruitingCampaignId());
			BeanUtilities.copyProperties(recruitingCampaign, recruitingCampaignDTO);

			if (recruitingCampaign.getCompanyUserGroup() != null && recruitingCampaignDTO.getCompanyUserGroupId() == null) {
				recruitingCampaign.setCompanyUserGroup(null);
			}

			if (recruitingCampaignDTO.getCompanyUserGroupId() != null) {
				if (!recruitingCampaignDTO.getCompanyUserGroupId().equals(BeanUtilities.getId(recruitingCampaign.getCompany()))) {
					recruitingCampaign.setCompanyUserGroup(userGroupService.findGroupById(recruitingCampaignDTO.getCompanyUserGroupId()));
				}
			}
		}

		// Company
		if (!recruitingCampaignDTO.getCompanyId().equals(BeanUtilities.getId(recruitingCampaign.getCompany())))
			recruitingCampaign.setCompany(profileService.findCompanyById(recruitingCampaignDTO.getCompanyId()));

		if (recruitingCampaignDTO.getCompanyLogoAssetId() != null) {
			recruitingCampaign.setCompanyLogo(assetService.get(recruitingCampaignDTO.getCompanyLogoAssetId()));
		} else {
			recruitingCampaign.setCompanyLogo(null);
		}

		// Recruiting Vendor
		if (recruitingCampaignDTO.getRecruitingVendorCode() != null)
			recruitingCampaign.setRecruitingVendor(new RecruitingVendor(recruitingCampaignDTO.getRecruitingVendorCode()));

		recruitingCampaignDAO.saveOrUpdate(recruitingCampaign);

		if (recruitingCampaign.getShortUrl() == null) {
			recruitingCampaign.setShortUrl(uriService.getShortUrl(recruitingCampaign.getRelativeURI()));
		}

		return recruitingCampaign;
	}

	@Override
	public RecruitingCampaign findRecruitingCampaign(Long recruitingCampaignId) {
		RecruitingCampaign campaign = recruitingCampaignDAO.get(recruitingCampaignId);
		return checkAndInitRecruitingCampaign(campaign, recruitingCampaignId);
	}

	@Override
	public RecruitingCampaign findRecruitingCampaign(Long companyId, Long recruitingCampaignId) {
		RecruitingCampaign campaign = recruitingCampaignDAO.get(recruitingCampaignId);

		if (campaign == null) {
			return null;
		}

		Assert.isTrue(campaign.getCompany().getId().equals(companyId));

		return checkAndInitRecruitingCampaign(campaign, recruitingCampaignId);
	}

	private RecruitingCampaign checkAndInitRecruitingCampaign(RecruitingCampaign campaign, Long recruitingCampaignId) {
		if (campaign == null || campaign.getDeleted()) {
			return null;
		}

		campaign.setClicks(recruitingCampaignDAO.countClicksByRecruitingCampaign(recruitingCampaignId));
		campaign.setUsers(recruitingCampaignDAO.countUsersByRecruitingCampaign(recruitingCampaignId));
		companyDAO.initialize(campaign.getCompany());
		if (campaign.getCompanyLogo() != null) {
			assetService.initialize(campaign.getCompanyLogo());
		}
		return campaign;
	}

	@Override
	public boolean existRecruitingCampaignByCompanyAndTitle(Long companyId, String recruitingCampaignTitle) {
		return recruitingCampaignDAO.existCampaignsForCompanyAndTitle(companyId, recruitingCampaignTitle);
	}

	@Override
	public RecruitingCampaign findRecruitingCampaign(String encryptedRecruitingCampaignId) {
		return findRecruitingCampaign(EncryptionUtilities.decryptLong(encryptedRecruitingCampaignId));
	}

	@Override
	public RecruitingCampaign findRecruitingCampaign(Long companyId, String encryptedRecruitingCampaignId) {
		return findRecruitingCampaign(companyId, EncryptionUtilities.decryptLong(encryptedRecruitingCampaignId));
	}

	@Override
	public RecruitingCampaignPagination findAllCampaignsByCompanyId(Long companyId, RecruitingCampaignPagination pagination) {
		return recruitingCampaignDAO.findAllCampaignsByCompanyId(companyId, pagination);
	}

	@Override
	public int countCampaignsForCompany(Long userId) {
		User user = userDAO.get(userId);

		return recruitingCampaignDAO.countCampaignsForCompany(user.getCompany().getId());
	}

	@Override
	public RecruitingCampaign activateRecruitingCampaign(Long companyId, Long recruitingCampaignId) throws Exception {
		return updateRecruitingCampaignActiveStatus(companyId, recruitingCampaignId, true);
	}

	@Override
	public RecruitingCampaign deactivateRecruitingCampaign(Long companyId, Long recruitingCampaignId) throws Exception {
		return updateRecruitingCampaignActiveStatus(companyId, recruitingCampaignId, false);
	}

	@Override
	public RecruitingCampaign deleteRecruitingCampaign(Long companyId, Long recruitingCampaignId) {
		RecruitingCampaign campaign = findRecruitingCampaign(companyId, recruitingCampaignId);
		if (campaign != null && !campaign.getDeleted()) {
			campaign.setDeleted(true);
			campaign.setActive(false);
			Random generator = new Random();
			Long randomNum = generator.nextLong();
			campaign.setTitle(campaign.getTitle() + "_" + randomNum.toString());
		}
		return campaign;
	}

	@Override
	public RecruitingCampaignUserPagination findAllRecruitingCampaignUsers(RecruitingCampaignUserPagination pagination) {
		Assert.notNull(pagination);
		return userDAO.findAllRecruitingCampaignUsers(pagination);
	}

	private RecruitingCampaign updateRecruitingCampaignActiveStatus(Long companyId, Long recruitingCampaignId, Boolean activeStatus) {
		RecruitingCampaign campaign = findRecruitingCampaign(companyId, recruitingCampaignId);
		campaign.setActive(activeStatus);
		return campaign;
	}

	@Override
	public RecruitingCampaign declineRecruitingCampaignUser(Long companyId, Long recruitingCampaignId, Long userId) {
		Assert.notNull(recruitingCampaignId);
		Assert.notNull(userId);

		RecruitingCampaign campaign = findRecruitingCampaign(companyId, recruitingCampaignId);
		User user = userService.getUser(userId);
		Assert.notNull(campaign, "Unable to find recruiting campaign");
		Assert.notNull(user, "Unable to find user");

		Assert.isTrue(campaign.getId().equals(user.getRecruitingCampaign().getId()), "Unable to decline user from Recruiting Campaign id " + recruitingCampaignId);

		LaneAssociation association = laneService.findActiveAssociationByUserIdAndCompanyId(userId, campaign.getCompany().getId());
		Assert.notNull(association, "Unable to find lane association between user " + userId + " and company " + campaign.getCompany().getId());
		Assert.isTrue(association.getApprovalStatus().isApproved(), "User lane 2 relationship is not approved");

		//Decline lane association
		laneService.updateLaneAssociationApprovalStatus(userId, campaign.getCompany().getId(), ApprovalStatus.DECLINED);
		laneService.removeUserFromCompanyLane(userId, campaign.getCompany().getId());

		//Remove from group (if any)
		if (campaign.getCompanyUserGroup() != null) {
			userGroupService.removeAssociation(campaign.getCompanyUserGroup().getId(), userId);
			//Remove invitation from group (if any)
			requestService.deleteInvitationToGroup(userId, campaign.getCompanyUserGroup().getId());
		}

		return campaign;
	}

	@Override
	public RecruitingCampaign approveRecruitingCampaignUser(Long companyId, Long recruitingCampaignId, Long userId) {
		Assert.notNull(recruitingCampaignId);
		Assert.notNull(userId);

		RecruitingCampaign campaign = findRecruitingCampaign(companyId, recruitingCampaignId);
		User user = userService.getUser(userId);
		Assert.notNull(campaign, "Unable to find recruiting campaign");
		Assert.notNull(user, "Unable to find user");

		Assert.isTrue(campaign.getId().equals(user.getRecruitingCampaign().getId()), "Unable to approve user from Recruiting Campaign id " + recruitingCampaignId);

		LaneAssociation association = laneService.findActiveAssociationByUserIdAndCompanyId(userId, campaign.getCompany().getId());
		Assert.notNull(association, "Unable to find lane association between user " + userId + " and company " + campaign.getCompany().getId());
		Assert.isTrue(association.getApprovalStatus().isApproved(), "User lane 2 relationship is not approved");

		laneService.updateLaneAssociationApprovalStatus(userId, campaign.getCompany().getId(), ApprovalStatus.APPROVED);

		UserGroup group = campaign.getCompanyUserGroup();
		if (group != null) {
			if (group.getOpenMembership())
				requestService.inviteUserToGroup(campaign.getCreatorId(), user.getId(), group.getId());
			else {
				//If the group is private, apply to the group
				userGroupService.applyToGroup(group.getId(), user.getId());
			}
		}

		return campaign;
	}

	@Override
	public List<RequestContext> getRequestContext(Long recruitingCampaignId) {
		List<RequestContext> contexts = Lists.newArrayList(RequestContext.PUBLIC);
		User currentUser = authenticationService.getCurrentUser();
		RecruitingCampaign campaign = recruitingCampaignDAO.get(recruitingCampaignId);

		if (campaign.getCreatorId().equals(currentUser.getId())) {
			contexts.add(RequestContext.OWNER);
		} else if (campaign.getCompany().equals(currentUser.getCompany())) {
			contexts.add(RequestContext.COMPANY_OWNED);
		}

		return contexts;
	}
}
