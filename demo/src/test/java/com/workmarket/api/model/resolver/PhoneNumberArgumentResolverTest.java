package com.workmarket.api.model.resolver;

import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.dto.PhoneNumberDTO;
import com.workmarket.api.v1.AddressbookController;
import org.apache.struts.mock.MockHttpServletRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;

import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PhoneNumberArgumentResolverTest {
	private PhoneNumberArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new PhoneNumberArgumentResolver();
	}

	/**
	 * Example:
	 * phones[<N>]=212-333-1188
	 * phones[<N>][ext]=123
	 * phones[<N>][type]={work, home, other}
	 */
	@Test
	public void evaluateArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("phones[0]", "212-333-1188");
		request.addParameter("phones[0][ext]", "555");
		request.addParameter("phones[0][type]", "work");

		request.addParameter("phones[1]", "646-333-5522");
		request.addParameter("phones[1][type]", "home");

		request.addParameter("phones[2]", "917-333-5522");
		request.addParameter("phones[2][type]", "other");

		request.addParameter("phones[3]", "718-423-2233");

		// if type not set defaults to WORK
		request.addParameter("phones[4]", null);

		PhoneNumberDTO[] phoneNumbers = resolver.evaluateArgument(request);
		assertNotNull(phoneNumbers);
		assertEquals(4, phoneNumbers.length);

		assertEquals("212-333-1188", phoneNumbers[0].getPhone());
		assertEquals("555", phoneNumbers[0].getExtension());
		assertEquals(ContactContextType.WORK, phoneNumbers[0].getContactContextType());

		assertEquals("646-333-5522", phoneNumbers[1].getPhone());
		assertNull(phoneNumbers[1].getExtension());
		assertEquals(ContactContextType.HOME, phoneNumbers[1].getContactContextType());

		assertEquals("917-333-5522", phoneNumbers[2].getPhone());
		assertNull(phoneNumbers[2].getExtension());
		assertEquals(ContactContextType.OTHER, phoneNumbers[2].getContactContextType());

		assertEquals("718-423-2233", phoneNumbers[3].getPhone());
		assertNull(phoneNumbers[3].getExtension());
		assertEquals(ContactContextType.WORK, phoneNumbers[3].getContactContextType());
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AddressbookController.class, "addClientContact");
		MethodParameter methodParameter = new MethodParameter(method, 6);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}
