package com.workmarket.dao.recruiting;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.recruiting.RecruitingCampaign;
import com.workmarket.domains.model.recruiting.RecruitingCampaignPagination;


public interface RecruitingCampaignDAO extends DAOInterface<RecruitingCampaign> {

	RecruitingCampaignPagination findAllCampaignsByCompanyId(Long companyId, RecruitingCampaignPagination pagination);

	int countCampaignsForCompany(Long companyId);

	int countClicksByRecruitingCampaign(Long recruitingCampaignId);

	int countUsersByRecruitingCampaign(Long recruitingCampaignId);

	boolean existCampaignsForCompanyAndTitle(Long companyId, String Title);
}
