package com.workmarket.velvetrope;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class DefaultTag extends SimpleTagSupport {
	@Override
	public void doTag() throws IOException, JspException {
		RopeTag ropeTag = (RopeTag) this.getParent();

		if (ropeTag == null) {
			throw new JspTagException("Velvet Rope: A Default Tag must be nested within a Rope Tag");
		}

		if (!ropeTag.isRendered()) {
			ropeTag.setRendered(true);
			this.getJspBody().invoke(null);
		}
	}
}
