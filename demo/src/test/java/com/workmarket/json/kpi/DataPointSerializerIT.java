package com.workmarket.json.kpi;

import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class DataPointSerializerIT extends BaseServiceIT {

	@Autowired JsonSerializationService jsonSerializationService;

	@Test
	public void serialize() throws Exception {
		DataPoint dataPoint = new DataPoint(1, 50, true, false);
		String json = jsonSerializationService.toJson(dataPoint);
		assertNotNull(json);
		assertTrue(StringUtils.contains(json, "50.0"));
		assertTrue(StringUtils.contains(json, "inProgressPeriod"));
		assertTrue(StringUtils.contains(json, "trendingUp"));
		assertTrue(StringUtils.contains(json, "x"));
		assertTrue(StringUtils.contains(json, "y"));
	}

	@Test
	public void deserialize() throws Exception {
		String json = "{\"x\":1,\"y\":50.0,\"inProgressPeriod\":true,\"trendingUp\":false}";
		DataPoint dataPoint = jsonSerializationService.fromJson(json, DataPoint.class);
		assertNotNull(dataPoint);
		assertTrue(dataPoint.isInProgressPeriod());
		assertFalse(dataPoint.isTrendingUp());
		assertTrue(dataPoint.isSetX());
		assertTrue(dataPoint.isSetY());
	}
}
