package com.workmarket.web.editors;


import com.workmarket.thrift.core.Status;
import org.apache.commons.lang.StringUtils;

import java.beans.PropertyEditorSupport;

public class ThriftStatusObjectEditor extends PropertyEditorSupport {
	@Override
	public String getAsText() {
		Status o = (Status)getValue();
		return (o == null) ? null : o.getCode();
	}

	@Override
	public void setAsText(String s) throws IllegalArgumentException {
		Status o = (StringUtils.isEmpty(s)) ? null : new Status().setCode(s);
		try {
			setValue(o);
		} catch (Exception e) {
			super.setAsText(s);
		}
	}
}
