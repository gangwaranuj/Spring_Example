package com.workmarket.service.business;

import com.workmarket.service.business.dto.WorkResourceDetail;
import com.workmarket.domains.model.WorkResourceStatusType;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.thrift.work.ResourceNote;
import com.workmarket.thrift.work.ResourceNoteType;
import com.workmarket.utility.DateUtilities;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Author: rocio
 */
@RunWith(MockitoJUnitRunner.class)
public class JsonSerializationServiceTest {

	@InjectMocks JsonSerializationServiceImpl jsonSerializationService;

	@Test
	public void toJson_withCalendar_success() {
		Calendar calendar = Calendar.getInstance();
		String iso = DateUtilities.getISO8601(calendar);
		String json = jsonSerializationService.toJson(calendar);
		assertTrue(json.contains(iso));
		//assertEquals(calendar, jsonSerializationService.fromJson(json, Calendar.class));
	}


	@Test
	public void toJson_withWorkResourceDetail_success() {
		WorkResourceDetail workResourceDetail = new WorkResourceDetail();
		workResourceDetail.setFirstName("someFirstName");
		workResourceDetail.setLastName("someLastName");
		workResourceDetail.setUserId(1L);
		workResourceDetail.setWorkResourceId(1000L);
		workResourceDetail.setWorkResourceStatusTypeCode(WorkResourceStatusType.ACCEPTED);
		workResourceDetail.setCompanyName("company1");


		String json = jsonSerializationService.toJson(workResourceDetail);
		assertTrue(json.contains("workResourceId"));
		assertTrue(json.contains("userId"));
		assertTrue(json.contains("workResourceStatusTypeCode"));
		assertTrue(json.contains("firstName"));
		assertTrue(json.contains("lastName"));
		assertTrue(json.contains("companyName"));
		assertTrue(json.contains("isAssignedToWork"));
		assertFalse(json.contains("appointmentFrom"));

		assertTrue(json.contains(WorkResourceStatusType.ACCEPTED));
		assertTrue(json.contains("1"));
		assertTrue(json.contains("1000"));
		assertTrue(json.contains("someFirstName"));
		assertTrue(json.contains("someLastName"));
		assertTrue(json.contains("company1"));
		assertTrue(json.contains("false"));

	}

	@Test
	public void toJson_withFullPricingStrategy_success() {
		FullPricingStrategy fullPricingStrategy = new FullPricingStrategy();
		fullPricingStrategy.setOverridePrice(BigDecimal.ONE);
		fullPricingStrategy.setMaxNumberOfUnits(BigDecimal.TEN);
		fullPricingStrategy.setFlatPrice(BigDecimal.valueOf(100));


		String json = jsonSerializationService.toJson(fullPricingStrategy);
		assertTrue(json.contains("overridePrice"));
		assertTrue(json.contains("maxNumberOfUnits"));
		assertTrue(json.contains("flatPrice"));

		assertTrue(json.contains("1"));
		assertTrue(json.contains("10"));
		assertTrue(json.contains("100"));
	}

	@Test
	public void toJson_withResourceNote_success() {
		ResourceNote resourceNote = new ResourceNote();
		resourceNote.setResourceId(1L);
		resourceNote.setOnBehalfOfUserName("behalf");
		resourceNote.setHoverType(ResourceNoteType.ACCEPT);

		String json = jsonSerializationService.toJson(resourceNote);
		assertTrue(json.contains("resourceId"));
		assertTrue(json.contains("onBehalfOfUserName"));
		assertTrue(json.contains("hoverType"));

		assertTrue(json.contains("1"));
		assertTrue(json.contains("behalf"));
		assertTrue(json.contains(ResourceNoteType.ACCEPT.toString()));
	}

}
