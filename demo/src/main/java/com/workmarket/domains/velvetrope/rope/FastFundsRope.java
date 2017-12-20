package com.workmarket.domains.velvetrope.rope;

import com.workmarket.velvetrope.Rope;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class FastFundsRope implements Rope {

 	private MutableBoolean hasFastFunds;

 	public FastFundsRope(MutableBoolean hasFastFunds) {
		this.hasFastFunds = hasFastFunds;
	}

 	@Override
 	public void enter() {
		hasFastFunds.setTrue();
	}
}
