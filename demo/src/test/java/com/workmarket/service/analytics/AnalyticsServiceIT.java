package com.workmarket.service.analytics;

import com.workmarket.domains.model.session.ImpressionType;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.dto.ImpressionDTO;
import com.workmarket.configuration.Constants;
import com.workmarket.test.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class AnalyticsServiceIT extends BaseServiceIT {

	@Autowired private AnalyticsService analyticsService;

	@Test
	public void saveOrUpdateImpression_success() throws Exception {
		ImpressionDTO dto = new ImpressionDTO();
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		dto.setImpressionTypeId(Long.valueOf(ImpressionType.RECRUITING.ordinal()));
		dto.setCampaignId(CAMPAIGN_RECRUITING_DIRTY_11_ID);
		dto.setReferrer("referrer");
		dto.setUserAgent("Firefox");
		dto.setField1Name("rcampagin");
		dto.setField1Value("2235");

		Assert.assertNotNull(analyticsService.saveOrUpdateImpression(dto));
	}

	@Test
	public void saveOrUpdateImpression_withLongReferrer_success() throws Exception {
		ImpressionDTO dto = new ImpressionDTO();
		authenticationService.setCurrentUser(Constants.WORKMARKET_SYSTEM_USER_ID);

		dto.setImpressionTypeId(Long.valueOf(ImpressionType.RECRUITING.ordinal()));
		dto.setCampaignId(CAMPAIGN_RECRUITING_DIRTY_11_ID);
		dto.setReferrer("Contrary to popular belief, Lorem Ipsum is not simply random text. It has roots in a piece of classical Latin literature from 45 BC, making it over 2000 years old. Richard McClintock, a Latin professor at Hampden-Sydney College in Virginia, looked up one of the more obscure Latin words, consectetur, from a Lorem Ipsum passage, and going through the cites of the word in classical literature, discovered the undoubtable source. Lorem Ipsum comes from sections 1.10.32 and 1.10.33 of de Finibus Bonorum et Malorum (The Extremes of Good and Evil) by Cicero, written in 45 BC. This book is a treatise on the theory of ethics, very popular during the Renaissance. The first line of Lorem Ipsum, Lorem ipsum dolor sit amet.., comes from a line in section 1.10.32.");
		dto.setUserAgent("Firefox");
		dto.setField1Name("rcampagin");
		dto.setField1Value("2235");

		Assert.assertNotNull(analyticsService.saveOrUpdateImpression(dto));
	}
}
