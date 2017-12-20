package com.workmarket.api.model.resolver;

import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.dto.EmailAddressDTO;
import com.workmarket.api.v1.AddressbookController;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


public class EmailAddressArgumentResolverTest {
	private EmailAddressArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new EmailAddressArgumentResolver();
	}

	/**
	 * Example:
	 * emails[<N>]=info@workmarket.com
	 * emails[<N>][type]={work, home, other}
	 */
	@Test
	public void evaluateArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("emails[0]", "john@workmarket.com");
		request.addParameter("emails[0][type]", "work");

		request.addParameter("emails[1]", "johnb@myhome.com");
		request.addParameter("emails[1][type]", "home");

		request.addParameter("emails[2]", "jeff@mobile.net");
		request.addParameter("emails[2][type]", "other");

		request.addParameter("emails[3]", null); // should not be added to the array
		request.addParameter("emails[3][type]", "work");

		EmailAddressDTO[] emails = resolver.evaluateArgument(request);
		assertNotNull(emails);
		assertEquals(3, emails.length);

		assertEquals("john@workmarket.com", emails[0].getEmail());
		assertEquals(ContactContextType.WORK, emails[0].getContactContextType());

		assertEquals("johnb@myhome.com", emails[1].getEmail());
		assertEquals(ContactContextType.HOME, emails[1].getContactContextType());

		assertEquals("jeff@mobile.net", emails[2].getEmail());
		assertEquals(ContactContextType.OTHER, emails[2].getContactContextType());
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AddressbookController.class, "addClientContact");
		MethodParameter methodParameter = new MethodParameter(method, 5);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}