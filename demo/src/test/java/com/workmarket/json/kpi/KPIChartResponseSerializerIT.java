package com.workmarket.json.kpi;

import com.google.common.collect.Lists;
import com.workmarket.domains.model.kpi.DataPoint;
import com.workmarket.domains.model.kpi.KPIChartResponse;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.JsonSerializationService;
import com.workmarket.test.IntegrationTest;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.apache.commons.collections.CollectionUtils.isEmpty;

/**
 * Author: rocio
 */
@RunWith(SpringJUnit4ClassRunner.class)
@Category(IntegrationTest.class)
public class KPIChartResponseSerializerIT extends BaseServiceIT {

	@Autowired JsonSerializationService jsonSerializationService;

	@Test
	public void serialize() throws Exception {
		KPIChartResponse kpiChartResponse = new KPIChartResponse();
		kpiChartResponse.setXAxisLabel("xlabel");
		kpiChartResponse.setYAxisLabel("ylabel");
		List<DataPoint> chartData = Lists.newArrayList();
		kpiChartResponse.setChartData(chartData);
		String json = jsonSerializationService.toJson(kpiChartResponse);
		assertNotNull(json);
		assertTrue(StringUtils.contains(json, "xlabel"));
		assertTrue(StringUtils.contains(json, "ylabel"));
		assertTrue(StringUtils.contains(json, "xAxisLabel"));
		assertTrue(StringUtils.contains(json, "yAxisLabel"));
	}

	@Test
	public void deserialize() throws Exception {
		String json = "{\"xAxisLabel\":\"xlabel\",\"yAxisLabel\":\"ylabel\"}";
		KPIChartResponse kpiChartResponse = jsonSerializationService.fromJson(json, KPIChartResponse.class);
		assertNotNull(kpiChartResponse);
		assertTrue(kpiChartResponse.isSetXAxisLabel());
		assertTrue(kpiChartResponse.isSetYAxisLabel());
		assertTrue(isEmpty(kpiChartResponse.getChartData()));
	}
}
