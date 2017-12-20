package com.workmarket.api.model.resolver;

import com.workmarket.thrift.core.User;
import com.workmarket.api.v1.AssignmentsCreateController;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.Assert.*;

public class LocationContactsArgumentResolverTest {
	private LocationContactsArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new LocationContactsArgumentResolver();
	}

	@Test
	public void evaluateLocationContactsArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("location_contacts[0][first_name]", "Wheeler");
		request.addParameter("location_contacts[0][last_name]", "Bryan");
		request.addParameter("location_contacts[0][email]", "wheelerbryanjr@gmail.com");
		request.addParameter("location_contacts[0][phone]", "678-429-7309");
		request.addParameter("location_contacts[0][phone_extension]", "123");

		User contact = resolver.evaluateArgument(0, request);
		assertNotNull(contact);
		assertEquals(contact.getName().getFirstName(), "Wheeler");
		assertEquals(contact.getName().getLastName(), "Bryan");
		assertEquals(contact.getEmail(), "wheelerbryanjr@gmail.com");
		assertEquals(contact.getProfile().getPhoneNumbers().get(0).getPhone(), "6784297309");
		assertEquals(contact.getProfile().getPhoneNumbers().get(0).getExtension(), "123");
	}

	@Test
	public void evaluateLocationContactsArgumentById() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("location_contacts[0][id]", "431");
		User contact = resolver.evaluateArgument(0, request);
		assertNotNull(contact);
		assertEquals(contact.getId(), 431L);
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AssignmentsCreateController.class, "create");
		MethodParameter methodParameter = new MethodParameter(method, 49);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}
