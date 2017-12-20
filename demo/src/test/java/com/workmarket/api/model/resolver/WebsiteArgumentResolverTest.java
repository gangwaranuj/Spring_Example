package com.workmarket.api.model.resolver;

import com.workmarket.domains.model.directory.ContactContextType;
import com.workmarket.service.business.dto.WebsiteDTO;
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

public class WebsiteArgumentResolverTest {
	private WebsiteArgumentResolver resolver;

	@Before
	public void setup() {
		resolver = new WebsiteArgumentResolver();
	}

	/**
	 * Example:
	 * websites[<N>]=www.workmarket.com
	 * websites[<N>][type]={work, home, other}
	 */
	@Test
	public void evaluateArgument() {
		MockHttpServletRequest request = new MockHttpServletRequest();
		request.addParameter("websites[0]", "www.workmarket.com");
		request.addParameter("websites[0][type]", "work");

		request.addParameter("websites[1]", "my.workmarket.com");
		request.addParameter("websites[1][type]", "home");

		request.addParameter("websites[2]", "other.workmarket.com");
		request.addParameter("websites[2][type]", "other");

		request.addParameter("websites[3]", null);
		request.addParameter("websites[3][type]", "other");

		WebsiteDTO[] websites = resolver.evaluateArgument(request);
		assertNotNull(websites);
		assertEquals(3, websites.length);

		assertEquals("www.workmarket.com", websites[0].getWebsite());
		assertEquals(ContactContextType.WORK, websites[0].getContactContextType());

		assertEquals("my.workmarket.com", websites[1].getWebsite());
		assertEquals(ContactContextType.HOME, websites[1].getContactContextType());

		assertEquals("other.workmarket.com", websites[2].getWebsite());
		assertEquals(ContactContextType.OTHER, websites[2].getContactContextType());
	}

	@Test
	public void supportsMethodParameter() throws Exception {
		Method method = BeanUtils.findDeclaredMethodWithMinimalParameters(AddressbookController.class, "addClient");
		MethodParameter methodParameter = new MethodParameter(method, 6);
		assertTrue(resolver.supportsParameter(methodParameter));
	}
}
