package com.workmarket.service.report.kpi.cache;

import com.workmarket.data.report.kpi.KPIRequest;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.test.IntegrationTest;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Calendar;

import static org.junit.Assert.assertNotNull;

/**
 * Author: rocio
 */
@SuppressWarnings("unchecked")
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class KPICacheIT extends BaseServiceIT {

	@Autowired private JsonSerializationService jsonSerializationService;

	@Test
	public void kpirequestToJson() throws Exception {
		KPIRequest request = new KPIRequest();
		request.setFrom(Calendar.getInstance());
		request.setTo(Calendar.getInstance());
		assertNotNull(jsonSerializationService.toJson(request));

	}
}
