package com.workmarket.service.business.integration.hooks.sugar;

import com.vividsolutions.jts.util.Assert;
import com.workmarket.service.infra.sugar.BuyerSignUpMockAdapterImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.mockito.Mockito.verify;

/**
 * User: iloveopt
 * Date: 5/19/14
 */

@RunWith(MockitoJUnitRunner.class)
public class SugarIntegrationServiceTest {

	@InjectMocks SugarIntegrationServiceImpl sugarIntegrationService;
	@Mock BuyerSignUpMockAdapterImpl buyerSignUpAdapter;

	@Test
	public void createLead() throws Exception{
		sugarIntegrationService.createLead(1L);
		verify(buyerSignUpAdapter).createLead(1L);
	}

	@Test
	public void getAccountOwner() throws Exception{
		sugarIntegrationService.getAccountOwner("1001");
		verify(buyerSignUpAdapter).getAccountOwner("1001");
	}

	@Test
	public void getAccountOwner_null_returnNA() throws Exception{
		Assert.equals("NA", sugarIntegrationService.getAccountOwner(null));
	}


}
