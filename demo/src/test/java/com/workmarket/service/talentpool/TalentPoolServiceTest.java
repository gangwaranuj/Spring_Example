package com.workmarket.service.talentpool;

import com.workmarket.business.talentpool.TalentPoolClient;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolGetParticipantsRequest;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipant;
import com.workmarket.business.talentpool.gen.Messages.TalentPoolParticipantsResponse;
import com.workmarket.common.core.RequestContext;
import com.workmarket.service.business.CompanyService;
import com.workmarket.service.business.UserGroupService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.web.WebRequestContextProvider;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import rx.Observable;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TalentPoolServiceTest {

	@Mock private CompanyService companyService;
	@Mock private TalentPoolParticipationAdapter talentPoolParticipationAdapter;
	@Mock private TalentPoolClient talentPoolClient;
	@Mock private UserGroupService userGroupService;
	@Mock private UserService userService;
	@Mock private WebRequestContextProvider webRequestContextProvider;
	@InjectMocks private TalentPoolServiceImpl talentPoolService;

	private RequestContext requestContext;
	private TalentPoolGetParticipantsRequest talentPoolGetParticipantsRequest;

	private static String VENDOR_UUID = "52f06c07-77c9-48e0-8f55-3d9e6231466c";
	private static String TALENT_POOL_UUID = "7b23532a-cc27-4b91-afc2-5fa4b6fd4318";

	@Before
	public void setup() {
		requestContext = new RequestContext("requestId", "DUMMY_TENANT_ID");
		when(webRequestContextProvider.getRequestContext()).thenReturn(requestContext);
		talentPoolGetParticipantsRequest = TalentPoolGetParticipantsRequest
				.newBuilder()
				.setTalentPoolUuid(TALENT_POOL_UUID)
				.addParticipantUuid(VENDOR_UUID)
				.build();
	}

	@Test
	public void isAlreadyParticipatingInTalentPool_true() {
		final TalentPoolParticipant talentPoolParticipant =
				TalentPoolParticipant.newBuilder()
				.setParticipantUuid(VENDOR_UUID)
				.build();
		final TalentPoolParticipantsResponse talentPoolParticipantsResponse =
				TalentPoolParticipantsResponse.newBuilder()
				.addTalentPoolParticipant(talentPoolParticipant)
				.build();

		when(talentPoolClient.getParticipants(talentPoolGetParticipantsRequest, requestContext))
				.thenReturn(Observable.just(talentPoolParticipantsResponse));

		assertTrue(talentPoolService.isAlreadyParticipatingInTalentPool(TALENT_POOL_UUID, VENDOR_UUID));
	}

	@Test
	public void isAlreadyParticipatingInTalentPool_false() {
		final TalentPoolParticipantsResponse talentPoolParticipantsResponse =
				TalentPoolParticipantsResponse.getDefaultInstance();

		when(talentPoolClient.getParticipants(talentPoolGetParticipantsRequest, requestContext))
				.thenReturn(Observable.just(talentPoolParticipantsResponse));

		assertFalse(talentPoolService.isAlreadyParticipatingInTalentPool(TALENT_POOL_UUID, VENDOR_UUID));
	}
}
