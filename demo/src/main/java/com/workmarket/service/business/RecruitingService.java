package com.workmarket.service.business;

import com.workmarket.domains.model.Invitation;
import com.workmarket.dto.RecruitingCampaignUserPagination;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;
import com.workmarket.service.business.dto.RecruitingCampaignDTO;
import com.workmarket.service.exception.HostServiceException;
import com.workmarket.service.infra.security.RequestContext;

import java.io.IOException;
import java.util.List;

public interface RecruitingService {

	public RecruitingCampaign saveOrUpdateRecruitingCampaign(RecruitingCampaignDTO recruitingCampaignDTO);

	public RecruitingCampaign findRecruitingCampaign(String encryptedRecruitingCampaignId);

	public RecruitingCampaign findRecruitingCampaign(Long recruitingCampaignId);

	public RecruitingCampaign findRecruitingCampaign(Long companyId, Long recruitingCampaignId);

	public RecruitingCampaign findRecruitingCampaign(Long companyId, String encryptedRecruitingCampaignId);

	/**
	 * Finds all the Recruiting Campaigns for a particular company.
	 * Use the filter in {@link com.workmarket.domains.model.recruiting.RecruitingCampaignPagination RecruitingCampaignPagination}  to return only active or inactive campaigns.
	 * Note that Pagination.setFilters expects a Map<String, String>
	 *
	 * @param companyId -
	 * @param pagination -
	 * @return {@link com.workmarket.domains.model.recruiting.RecruitingCampaignPagination RecruitingCampaignPagination}
	 */
	public RecruitingCampaignPagination findAllCampaignsByCompanyId(Long companyId, RecruitingCampaignPagination pagination);

	/**
	 * Counts the Active Recruiting campaigns for a particular company.
	 *
	 * @param userId -
	 * @return int
	 */
	public int countCampaignsForCompany(Long userId);

	/**
	 * Sets the active status to TRUE.
	 *
	 * @param recruitingCampaignId -
	 * @throws Exception
	 */
	public RecruitingCampaign activateRecruitingCampaign(Long companyId, Long recruitingCampaignId) throws Exception;

	/**
	 * Sets the active status to FALSE.
	 *
	 * @param recruitingCampaignId -
	 * @throws Exception
	 */
	public RecruitingCampaign deactivateRecruitingCampaign(Long companyId, Long recruitingCampaignId) throws Exception;

	public RecruitingCampaign deleteRecruitingCampaign(Long companyId, Long recruitingCampaignId);

	public RecruitingCampaignUserPagination findAllRecruitingCampaignUsers(RecruitingCampaignUserPagination pagination);

	public RecruitingCampaign declineRecruitingCampaignUser(Long companyId, Long recruitingCampaignId, Long userId);

	public RecruitingCampaign approveRecruitingCampaignUser(Long companyId, Long recruitingCampaignId, Long userId);

	public boolean existRecruitingCampaignByCompanyAndTitle(Long companyId, String recruitingCampaignTitle);

	/**
	 * Get the current user's authorization context for the requested entity.
	 *
	 * @param recruitingCampaignId -
	 * @return List<RequestContext>
	 */
	List<RequestContext> getRequestContext(Long recruitingCampaignId);
}
