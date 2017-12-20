package com.workmarket.dao.session;

import com.workmarket.dao.DAOInterface;
import com.workmarket.domains.model.session.Impression;
import com.workmarket.dto.CampaignStatisticsDTO;


public interface ImpressionDAO extends DAOInterface<Impression> {

    CampaignStatisticsDTO newCampaignStatistics(Long impressionTypeId, Long campaignId);
}
