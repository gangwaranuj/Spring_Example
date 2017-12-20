package com.workmarket.velvetrope;

import com.workmarket.domains.model.User;
import com.workmarket.service.business.BaseServiceIT;
import com.workmarket.service.business.RegistrationService;
import com.workmarket.service.business.UserService;
import com.workmarket.service.infra.business.AuthenticationService;
import com.workmarket.domains.authentication.model.ExtendedUserDetails;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsOptionsService;
import com.workmarket.domains.authentication.services.ExtendedUserDetailsService;
import com.workmarket.test.mock.auth.AuthenticationMock;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.mock.web.MockPageContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.jsp.JspContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.JspFragment;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

public class BaseTagIT extends BaseServiceIT {
	@Autowired WebApplicationContext webApplicationContext;
	@Autowired ExtendedUserDetailsService extendedUserDetailsService;
	@Autowired RegistrationService registrationService;
	@Autowired UserService userService;
	@Autowired AuthenticationService authenticationService;

	protected MockPageContext mockPageContext;

	@Before
	public final void before() throws Exception {
		mockPageContext = new MockPageContext(
			webApplicationContext.getServletContext()
		);
	}

	String getContent() throws UnsupportedEncodingException {
		return ((MockHttpServletResponse)mockPageContext.getResponse()).getContentAsString();
	}

	class MockJspFragment extends JspFragment {
		private String content;

		public MockJspFragment(String content) {
			this.content = content;
		}

		@Override
		public void invoke(Writer out) throws JspException, IOException {
			getJspContext().getOut().write(content);
		}

		@Override
		public JspContext getJspContext() {
			return mockPageContext;
		}
	}

	void authenticateSimply() throws Exception {
		User worker = newRegisteredWorker();
		ExtendedUserDetailsOptionsService.OPTION[] options = {ExtendedUserDetailsOptionsService.OPTION.COMPANY};
		ExtendedUserDetails details = (ExtendedUserDetails) extendedUserDetailsService.loadUserByEmail(worker.getEmail(), options);
		AuthenticationMock authentication = new AuthenticationMock(details);
		SecurityContextHolder.getContext().setAuthentication(authentication);
	}
}
