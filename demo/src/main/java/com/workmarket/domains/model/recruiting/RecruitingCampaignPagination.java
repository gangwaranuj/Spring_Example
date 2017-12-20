package com.workmarket.domains.model.recruiting;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RecruitingCampaignPagination extends AbstractPagination<RecruitingCampaign> implements Pagination<RecruitingCampaign> {
	public enum FILTER_KEYS {
		ACTIVE
	}

	public enum SORTS {
		CAMPAIGN_TITLE, CAMPAIGN_ID, CAMPAIGN_DATE, CLICKS, SIGNUPS
	}
}
