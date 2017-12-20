package com.workmarket.service.analytics;

import com.workmarket.domains.model.analytics.ResourceScoreCard;
import com.workmarket.domains.model.analytics.ScoreCard;
import com.workmarket.domains.model.analytics.VendorScoreCard;
import com.workmarket.domains.model.session.Impression;
import com.workmarket.dto.CampaignStatisticsDTO;
import com.workmarket.service.business.dto.ImpressionDTO;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public interface AnalyticsService {

	Impression saveOrUpdateImpression(ImpressionDTO impressionDTO) throws Exception;

	CampaignStatisticsDTO newCampaignStatistics(Long impressionTypeId, Long campaignId);

	Map<Long, Integer> countDistinctBlockingCompaniesByUser(Calendar fromDate);

	Map<Long, Integer> countDistinctBlockingCompaniesByUser(Calendar fromDate, List<Long> userIds);

	Map<Long, Integer> countRepeatedClientsByUser(Calendar fromDate, List<Long> userIds);

	ScoreCard getResourceScoreCard(long userId);

	ScoreCard getVendorScoreCard(long vendorId);

	ScoreCard getResourceScoreCardForCompany(long companyId, long userId);

	ScoreCard getVendorScoreCardForCompany(long companyId, long vendorId);

	ScoreCard getBuyerScoreCardByUserId(Long userId);

	ScoreCard getBuyerScoreCardByCompanyId(Long companyId);

	Map<Long, ResourceScoreCard> getResourceScoreCards(List<Long> userIds);

	Map<Long, VendorScoreCard> getVendorScoreCards(List<Long> vendorIds);

	Map<Long, ResourceScoreCard> getResourceScoreCardsForCompany(Long companyId, List<Long> userIds);

	Map<Long, VendorScoreCard> getVendorScoreCardsForCompany(Long companyId, List<Long> vendorIds);
}
