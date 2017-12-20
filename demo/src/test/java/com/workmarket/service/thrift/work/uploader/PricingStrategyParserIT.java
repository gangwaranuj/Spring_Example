package com.workmarket.service.thrift.work.uploader;

import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.thrift.work.Work;
import com.workmarket.service.business.upload.parser.PricingStrategyParser;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildData;
import com.workmarket.service.business.upload.parser.WorkUploaderBuildResponse;
import com.workmarket.utility.CollectionUtilities;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@Ignore
public class PricingStrategyParserIT extends BaseServiceIT {

	@Autowired private PricingStrategyParser pricingParser;

	@Test
	public void testerooni() throws Exception {
		WorkUploaderBuildResponse response = new WorkUploaderBuildResponse(new Work());
		pricingParser.build(response, new WorkUploaderBuildData().setTypes(CollectionUtilities.newStringMap()));

		Assert.assertTrue(response.getWork().isSetPricing());
	}
}
