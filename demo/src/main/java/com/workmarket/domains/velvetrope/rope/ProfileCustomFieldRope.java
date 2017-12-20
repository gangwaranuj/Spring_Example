package com.workmarket.domains.velvetrope.rope;

import com.workmarket.velvetrope.Rope;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ProfileCustomFieldRope implements Rope {
	private MutableBoolean enableProfileCustomField;

	public ProfileCustomFieldRope(final MutableBoolean enableProfileCustomField) {
		this.enableProfileCustomField = enableProfileCustomField;
	}

	@Override
	public void enter() {
		enableProfileCustomField.setValue(true);
	}
}