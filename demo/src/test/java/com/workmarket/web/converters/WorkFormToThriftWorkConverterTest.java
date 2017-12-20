package com.workmarket.web.converters;

import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.SecurityContextFacade;
import com.workmarket.domains.model.pricing.FlatPricePricingStrategy;
import com.workmarket.domains.model.pricing.FullPricingStrategy;
import com.workmarket.domains.model.pricing.InternalPricingStrategy;
import com.workmarket.domains.model.pricing.PricingStrategyType;
import com.workmarket.domains.work.service.WorkService;
import com.workmarket.service.business.PricingService;
import com.workmarket.thrift.work.Work;
import com.workmarket.web.forms.work.WorkForm;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class WorkFormToThriftWorkConverterTest {

	@Mock private SecurityContextFacade securityContextFacade;
	@Mock private PricingService pricingService;
	@Mock private WorkService workService;
	@InjectMocks private WorkFormToThriftWorkConverter workFormToThriftWorkConverter;

	@Before
	public void init() {

		ExtendedUserDetails userDetails = mock(ExtendedUserDetails.class);
		when(userDetails.getTimeZoneId()).thenReturn("");
		when(securityContextFacade.getCurrentUser()).thenReturn(userDetails);

		InternalPricingStrategy internalPricingStrategy = mock(InternalPricingStrategy.class);
		FullPricingStrategy internalFullPricingStrategy = mock(FullPricingStrategy.class);
		when(internalFullPricingStrategy.getPricingStrategyType()).thenReturn(PricingStrategyType.INTERNAL);
		when(internalFullPricingStrategy.getPricingStrategy()).thenReturn(internalPricingStrategy);
		when(internalPricingStrategy.getFullPricingStrategy()).thenReturn(internalFullPricingStrategy);
		when(pricingService.findPricingStrategyById(eq(PricingStrategyType.getId(PricingStrategyType.INTERNAL)))).thenReturn(internalPricingStrategy);

		FlatPricePricingStrategy flatPricingStrategy = mock(FlatPricePricingStrategy.class);
		FullPricingStrategy flatFullPricingStrategy = mock(FullPricingStrategy.class);
		when(flatFullPricingStrategy.getPricingStrategyType()).thenReturn(PricingStrategyType.FLAT);
		when(flatFullPricingStrategy.getPricingStrategy()).thenReturn(flatPricingStrategy);
		when(flatPricingStrategy.getFullPricingStrategy()).thenReturn(flatFullPricingStrategy);
		when(pricingService.findPricingStrategyById(eq(PricingStrategyType.getId(PricingStrategyType.FLAT)))).thenReturn(flatPricingStrategy);
	}

	@Test
	public void convertWorkForm_internalPricing() {
		WorkForm src = new WorkForm();
		src.setPricing_mode("spend");
		src.setShow_in_feed(false);
		src.setCheck_in(true);
		src.setCheck_in_call_required(false);
		src.setPricing(PricingStrategyType.getId(PricingStrategyType.INTERNAL));
		Work dest = new Work();

		workFormToThriftWorkConverter.convert(src, dest);

		assertEquals(dest.getPricing().getType(), PricingStrategyType.INTERNAL);
	}

	@Test
	public void convertWorkForm_Offline() {
		WorkForm src = new WorkForm();
		src.setPricing_mode("spend");
		src.setShow_in_feed(false);
		src.setCheck_in(true);
		src.setCheck_in_call_required(false);
		src.setOfflinePayment(true);
		src.setPricing(PricingStrategyType.getId(PricingStrategyType.FLAT));
		Work dest = new Work();

		workFormToThriftWorkConverter.convert(src, dest);

		assertEquals(true, dest.getPricing().isOfflinePayment());
	}

	@Test
	public void convertWorkForm_internalPricingAndOffline() {
		WorkForm src = new WorkForm();
		src.setPricing_mode("spend");
		src.setShow_in_feed(false);
		src.setCheck_in(true);
		src.setCheck_in_call_required(false);
		src.setOfflinePayment(true);
		src.setPricing(PricingStrategyType.getId(PricingStrategyType.INTERNAL));
		Work dest = new Work();

		workFormToThriftWorkConverter.convert(src, dest);

		assertEquals(dest.getPricing().getType(), PricingStrategyType.INTERNAL);
		assertEquals(false, dest.getPricing().isOfflinePayment());
	}
}
