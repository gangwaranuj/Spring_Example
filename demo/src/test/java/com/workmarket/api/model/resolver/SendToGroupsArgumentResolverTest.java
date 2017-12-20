package com.workmarket.api.model.resolver;

import com.workmarket.search.request.user.PeopleSearchRequest;
import com.workmarket.api.v1.AssignmentsCreateController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class SendToGroupsArgumentResolverTest {
	private SendToGroupsArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new SendToGroupsArgumentResolver();
	}

	@Test
	public void evaluateArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("send_to_groups[0]", "1234");
		request.addParameter("send_to_groups[1]", "5678");
		request.addParameter("send_to_groups[2]", "8958");
		request.addParameter("send_to_groups[3]", "");
		request.addParameter("send_to_groups[4]", "ABC");

		PeopleSearchRequest peopleSearchRequest = resolver.evaluateArgument(request);
		assertTrue(peopleSearchRequest.isSetGroupFilter());
		assertEquals(3, peopleSearchRequest.getGroupFilterSize());
		assertTrue(peopleSearchRequest.getGroupFilter().contains(1234L));
		assertTrue(peopleSearchRequest.getGroupFilter().contains(5678L));
		assertTrue(peopleSearchRequest.getGroupFilter().contains(8958L));
	}

	@Test
	public void evaluateNullArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		PeopleSearchRequest peopleSearchRequest = resolver.evaluateArgument(request);
		assertNull(peopleSearchRequest);
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AssignmentsCreateController.class, "create");
		MethodParameter methodParameter = new MethodParameter(method, 53);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}
