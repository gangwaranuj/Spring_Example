package com.workmarket.json;

import com.workmarket.domains.model.WorkStatusType;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.junit.Assert;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class WorkStatusTypeSerializerIT extends BaseServiceIT {

	@Autowired JsonSerializationService jsonSerializationService;

	@Test
	public void serialize() throws Exception {
		WorkStatusType workStatusType = new WorkStatusType(WorkStatusType.ACTIVE);
		String json = jsonSerializationService.toJson(workStatusType);
		assertNotNull(json);
		assertTrue(StringUtils.contains(json, WorkStatusType.ACTIVE));
		assertTrue(StringUtils.contains(json, "code"));
	}

	@Test
	public void deserialize() throws Exception {
		String json = "{\"code\":\"active\"}";
		WorkStatusType workStatusType = jsonSerializationService.fromJson(json, WorkStatusType.class);
		assertNotNull(workStatusType);
		Assert.assertEquals(workStatusType.getCode(), WorkStatusType.ACTIVE);
	}
}
