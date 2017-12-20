package com.workmarket.domains.velvetrope.rope;

import com.workmarket.velvetrope.Rope;
import org.apache.commons.lang3.mutable.MutableBoolean;

public class MarketplaceFilterHidingRope implements Rope {
	private MutableBoolean shouldHideMarketplaceFilters;

	public MarketplaceFilterHidingRope(MutableBoolean shouldHideMarketplaceFilters) {
		this.shouldHideMarketplaceFilters = shouldHideMarketplaceFilters;
	}

	@Override
	public void enter() {
		shouldHideMarketplaceFilters.setTrue();
	}
}
