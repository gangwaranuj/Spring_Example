package com.workmarket.domains.velvetrope.rope;

import com.google.common.collect.ImmutableList;
import com.workmarket.velvetrope.Rope;
import com.workmarket.web.forms.feed.FeedRequestParams;

public class HideWorkFeedRope implements Rope {
	private final FeedRequestParams params;
	private final Long companyId;

	public HideWorkFeedRope(FeedRequestParams params, Long companyId) {
		this.params = params;
		this.companyId = companyId;
	}

	public void enter() {
		params.setExclusiveCompanyIds(ImmutableList.of(companyId));
	}
}
