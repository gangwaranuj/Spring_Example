package com.workmarket.dto;

import com.workmarket.domains.model.AbstractPagination;
import com.workmarket.domains.model.Pagination;

public class RecruitingCampaignUserPagination extends AbstractPagination<RecruitingCampaignUser> implements Pagination<RecruitingCampaignUser> {

    public RecruitingCampaignUserPagination() {
    }

    public RecruitingCampaignUserPagination(boolean returnAllRows) {
        super(returnAllRows);
    }

    public enum FILTER_KEYS {
    	REGISTRATION_DATE_FROM, REGISTRATION_DATE_TO, CAMPAIGN_DATE_FROM, CAMPAIGN_DATE_TO, STATE, ZIP_CODE, CAMPAIGN_ID, GROUP_APPROVAL_STATUS
	}

	public enum SORTS {
		FIRST_NAME, LAST_NAME, CAMPAIGN_TITLE, ZIP_CODE, CITY, STATE, REGISTRATION_DATE, CAMPAIGN_LAUNCH_DATE, GROUP_APPROVAL_STATUS
	}
}
