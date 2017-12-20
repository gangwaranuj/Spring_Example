package com.workmarket.json.kpi;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.kpi.Filter;
import com.workmarket.domains.model.kpi.KPIReportFilter;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.apache.commons.collections.CollectionUtils.isNotEmpty;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class KPIFilterSerializerIT extends BaseServiceIT {

	@Autowired JsonSerializationService jsonSerializationService;

	@Test
	public void serialize() throws Exception {
		Filter filter = new Filter();
		filter.setName(KPIReportFilter.ACTIVE_RESOURCE_USER_ID);
		filter.setValues(Lists.newArrayList("1"));
		String json = jsonSerializationService.toJson(filter);
		assertNotNull(json);
		assertTrue(StringUtils.contains(json, KPIReportFilter.ACTIVE_RESOURCE_USER_ID.toString()));
		assertTrue(StringUtils.contains(json, "name"));
		assertTrue(StringUtils.contains(json, "values"));
		assertTrue(StringUtils.contains(json, "1"));
	}

	@Test
	public void deserialize() throws Exception {
		String json = "{\"name\":\"ACTIVE_RESOURCE_USER_ID\",\"values\":[\"1\"]}";
		Filter filter = jsonSerializationService.fromJson(json, Filter.class);
		assertNotNull(filter);
		assertTrue(filter.isSetName());
		assertTrue(isNotEmpty(filter.getValues()));
	}
}
