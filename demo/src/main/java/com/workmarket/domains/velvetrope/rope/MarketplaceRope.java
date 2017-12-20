package com.workmarket.domains.velvetrope.rope;

import com.workmarket.velvetrope.Rope;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class MarketplaceRope implements Rope {
	private MutableBoolean hasMarketplace;

	public MarketplaceRope(MutableBoolean hasMarketplace) {
		this.hasMarketplace = hasMarketplace;
	}

	@Override
	public void enter() {
		hasMarketplace.setTrue();
	}
}
