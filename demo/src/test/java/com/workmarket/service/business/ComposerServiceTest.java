package com.workmarket.service.business;

import com.workmarket.common.core.RequestContext;
import com.workmarket.core.composer.ComposerClient;
import com.workmarket.core.composer.gen.Messages.UserIdentity;
import com.workmarket.core.composer.gen.Messages.Scope;
import com.workmarket.core.composer.gen.Messages.Set;
import com.workmarket.core.composer.gen.Messages.Get;
import com.workmarket.core.composer.gen.Messages.Status;
import com.workmarket.core.composer.gen.Messages.DataResp;
import com.workmarket.core.composer.gen.Messages.Data;
import com.workmarket.core.composer.gen.Messages.NamespaceAndNameToValue;
import com.workmarket.domains.model.composer.ComposerField;
import com.workmarket.domains.model.composer.ComposerFieldInstance;
import com.workmarket.domains.model.composer.ComposerFieldResponse;
import com.workmarket.service.composer.ComposerServiceImpl;
import cucumber.deps.difflib.Delta;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import rx.Observable;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ComposerServiceTest {

	@Mock private ComposerClient composerClient;
	@InjectMocks private ComposerServiceImpl composerService;

	private final String KEY = "byline";
	private final String VALUE = "test";
	private final String TYPE = "profile";
	private final String USER_ID = "userid-5ceac34c-483a-f303-15ff-54bc46c2c70c";
	private final String PROFILE_ID = "profileid-6b06b089-bd27-e91e-9cd0-1e65ea8f8a3e";
	private final String COMPANY_ID = "companyid-90bbeac0-1eba-2aff-7c15-c0d32bf46ecb";
	private final String FIRST_ERROR_MESSAGE = "first error message";
	private final String SECOND_ERROR_MESSAGE = "second error message";

	private Set set;
	private Get get;
	private Scope scope;
	private RequestContext context;
	private UserIdentity userIdentity;
	private NamespaceAndNameToValue namespaceAndNameToValue;

	@Before
	public void setUp() throws Exception {
		userIdentity = UserIdentity.newBuilder()
			.setCompanyUuid(COMPANY_ID)
			.setUserUuid(USER_ID)
			.build();

		scope = Scope.newBuilder()
			.setScopeType(TYPE)
			.setUuid(PROFILE_ID)
			.build();

		namespaceAndNameToValue = NamespaceAndNameToValue.newBuilder()
			.setNamespace(COMPANY_ID)
			.setName(KEY)
			.setValue(VALUE)
			.build();

		set = Set.newBuilder()
			.setUserIdentity(userIdentity)
			.setScope(scope)
			.addValues(namespaceAndNameToValue)
			.build();

		get = Get.newBuilder()
			.setUserIdentity(userIdentity)
			.setScope(scope)
			.build();

		context = new RequestContext("RID", "TENANT");
		context.setOriginIp("127.0.0.1");
		context.setOrigin("localhost");
		context.setCompanyId(COMPANY_ID);
		context.setUserId(USER_ID);
	}

	@Test
	public void setByline_successTest() {
		Status status = Status.newBuilder().setSuccess(true).build();
		when(composerClient.set(set, context)).thenReturn(Observable.just(status));

		List<ComposerFieldInstance> fields = new LinkedList<>();
		fields.add(new ComposerFieldInstance(TYPE, PROFILE_ID, KEY, VALUE));

		List<ComposerFieldResponse> response = composerService.setValues(context, fields);

		ComposerFieldResponse fieldResponse = response.get(0);
		assertEquals(1, response.size());
		assertEquals(true, fieldResponse.isSuccess());
		assertEquals(0, fieldResponse.getMessages().size());
	}

	@Test
	public void setByline_unsuccessTest() {
		Status status = Status.newBuilder().setSuccess(false).addMessage(FIRST_ERROR_MESSAGE).addMessage(SECOND_ERROR_MESSAGE).build();
		when(composerClient.set(set, context)).thenReturn(Observable.just(status));

		List<ComposerFieldInstance> fields = new LinkedList<>();
		fields.add(new ComposerFieldInstance(TYPE, PROFILE_ID, KEY, VALUE));

		List<ComposerFieldResponse> response = composerService.setValues(context, fields);

		ComposerFieldResponse fieldResponse = response.get(0);
		assertFalse(fieldResponse.isSuccess());
		assertEquals(FIRST_ERROR_MESSAGE, fieldResponse.getMessages().get(0));
		assertEquals(SECOND_ERROR_MESSAGE, fieldResponse.getMessages().get(1));
		assertEquals(1, response.size());
	}

	@Test
	public void getByline_emptyValueTest() {
		DataResp resp = DataResp.newBuilder().build();
		when(composerClient.get(get, context)).thenReturn(Observable.just(resp));

		List<ComposerField> fields = new LinkedList<>();
		fields.add(new ComposerField(TYPE, PROFILE_ID, KEY));

		List<ComposerFieldInstance> response = composerService.getValues(context, fields);

		assertEquals(0, response.size());
	}

	@Test
	public void getByline_someValueTest() {
		Data data = Data.newBuilder().setNamespace(COMPANY_ID).setName(KEY).setValue(VALUE).build();
		DataResp resp = DataResp.newBuilder().addData(data).build();
		when(composerClient.get(get, context)).thenReturn(Observable.just(resp));

		List<ComposerField> fields = new LinkedList<>();
		fields.add(new ComposerField(TYPE, PROFILE_ID, KEY));

		List<ComposerFieldInstance> response = composerService.getValues(context, fields);

		ComposerFieldInstance fieldInstance = response.get(0);

		assertEquals(VALUE, fieldInstance.getValue());
		assertEquals(KEY, fieldInstance.getKey());
		assertEquals(TYPE, fieldInstance.getType());
		assertEquals(PROFILE_ID, fieldInstance.getUuid());
		assertEquals(1, response.size());
	}
}