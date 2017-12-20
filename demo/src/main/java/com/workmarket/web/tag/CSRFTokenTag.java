package com.workmarket.web.tag;

import com.workmarket.service.web.CSRFTokenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;

import static org.apache.commons.lang.StringUtils.isBlank;

/**
 * Outputs the session's CSRF Token, either inside an input tag or raw
 *
 * Created by nick on 7/12/13 12:21 PM
 */
public class CSRFTokenTag extends TagSupport {
	private static final long serialVersionUID = 745177955805541350L;
	private static final Logger logger = LoggerFactory.getLogger(CSRFTokenTag.class);
	private boolean plainToken = false;

	@Override
	public int doStartTag() throws JspException {
		final WebApplicationContext wc = RequestContextUtils.getWebApplicationContext(
				super.pageContext.getRequest(), super.pageContext.getServletContext()
		);
		final CSRFTokenService csrfTokenService = (CSRFTokenService) wc.getBean("csrfTokenService");

		final String token = csrfTokenService.getTokenFromSession((HttpServletRequest) super.pageContext.getRequest());

		if (!isBlank(token))
			try {
				pageContext.getOut().write(plainToken ?
						token :
						String.format("<input type=\"hidden\" name=\"%1$s\" id=\"%1$s\" value=\"%2$s\" />",
								CSRFTokenService.TOKEN_PARAMETER_NAME, token)
				);
			} catch (IOException e) {
				logger.error("", e);
			}
		return SKIP_BODY;
	}

	@Override
	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}

	public boolean isPlainToken() {
		return plainToken;
	}

	public void setPlainToken(boolean plainToken) {
		this.plainToken = plainToken;
	}
}
