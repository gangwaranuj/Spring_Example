package com.workmarket.velvetrope;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class RopeTag extends SimpleTagSupport {

	private boolean rendered = false;

	@Override
	public void doTag() throws IOException, JspException {
		this.getJspBody().invoke(null);
	}

	public boolean isRendered() {
		return rendered;
	}

	public void setRendered(boolean rendered) {
		this.rendered = rendered;
	}
}
