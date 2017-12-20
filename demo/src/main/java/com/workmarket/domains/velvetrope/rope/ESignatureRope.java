package com.workmarket.domains.velvetrope.rope;

import com.workmarket.velvetrope.Rope;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class ESignatureRope implements Rope {
	private MutableBoolean enableESignature;

	public ESignatureRope(final MutableBoolean enableESignature) {
		this.enableESignature = enableESignature;
	}

	@Override
	public void enter() {
		enableESignature.setValue(true);
	}
}
