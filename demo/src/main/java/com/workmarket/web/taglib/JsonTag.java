package com.workmarket.web.taglib;

import com.workmarket.service.business.JsonSerializationService;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.TagSupport;

public class JsonTag extends TagSupport {
	private JsonSerializationService jsonSerializationService;
	private Object object;

	@Override
	public void setPageContext(PageContext pageContext) {
		super.setPageContext(pageContext);

		ServletContext servletContext = pageContext.getServletContext();
		ApplicationContext applicationContext = (ApplicationContext)servletContext.getAttribute(
													WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		jsonSerializationService = applicationContext.getBean(JsonSerializationService.class);
	}

	@Override
	public int doStartTag() throws JspException {
		try {
			pageContext.getOut().print(jsonSerializationService.toJson(object));
		} catch (Exception e) {
			throw new JspException(e);
		}
		return SKIP_BODY;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}
